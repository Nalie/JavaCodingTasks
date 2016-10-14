package test.servlet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import test.exception.InvalidPasswordException;
import test.exception.NoSuchUserException;
import test.exception.UserIsAlreadyExistsException;
import test.model.User;
import test.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nyapparova on 12.10.2016.
 */
public class UserServlet extends HttpServlet {
    private static final String ACTION = "action";

    private static final String CREATE_AGT = "CREATE-AGT";
    private static final String GET_BALANCE = "GET-BALANCE";

    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";
    private static final String REQUEST_TYPE = "request-type";
    private static final String EXTRA = "extra";
    private static final String NAME = "name";
    private static final String RESPONSE = "response";
    private static final String RESULT_CODE = "result-code";
    private static final String BALANCE = "balance";

    private final UserService userService;

    public UserServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            synchronized (this) {
                wait(20000);
            }
        } catch (InterruptedException e){
            System.out.println(e);
        }
        System.out.println("waiting finished at " + System.currentTimeMillis()/1000);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Map<String, String> map = parseRequest(req.getInputStream());
            User user = new User(map.get(LOGIN), map.get(PASSWORD));
            if (CREATE_AGT.equals(map.get(ACTION))) {
                userService.addUser(user);
                createResponse(resp, 0);
            } else if (GET_BALANCE.equals(map.get(ACTION))) {
                createResponse(resp, 0, userService.getUserBalance(user));
            }
        } catch (IOException | SQLException e) {
            createResponse(resp, 2);
        } catch (UserIsAlreadyExistsException e) {
            createResponse(resp, 1);
        } catch (NoSuchUserException e) {
            createResponse(resp, 3);
        } catch (InvalidPasswordException e) {
            createResponse(resp, 4);
        }
    }

    private static Map<String, String> parseRequest(InputStream is) {
        Map<String, String> ret = new HashMap<>(3);
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();
            ret.put(ACTION, doc.getElementsByTagName(UserServlet.REQUEST_TYPE).item(0).getTextContent());
            NodeList list = doc.getElementsByTagName(UserServlet.EXTRA);
            for (int i = 0; i < list.getLength(); i++) {
                Element el = ((Element) list.item(i));
                ret.put(el.getAttribute(NAME), el.getTextContent());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return ret;
    }

    private void createResponse(HttpServletResponse resp, int code) throws IOException {
        createResponse(resp, code, null);
    }

    private void createResponse(HttpServletResponse resp, int code, BigDecimal balance) {
        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            doc.setXmlVersion("1.0");
            Element rootElement = doc.createElement(RESPONSE);
            doc.appendChild(rootElement);
            Element resultCodeElement = doc.createElement(RESULT_CODE);
            resultCodeElement.appendChild(doc.createTextNode(String.valueOf(code)));
            rootElement.appendChild(resultCodeElement);
            if (balance != null) {
                Element balanceElement = doc.createElement(EXTRA);
                balanceElement.setAttribute(NAME, BALANCE);
                DecimalFormat df = new DecimalFormat("0.00");
                DecimalFormatSymbols decimalFormatSymbols = df.getDecimalFormatSymbols();
                decimalFormatSymbols.setDecimalSeparator('.');
                df.setDecimalFormatSymbols(decimalFormatSymbols);
                balanceElement.appendChild(doc.createTextNode(df.format(balance)));
                rootElement.appendChild(balanceElement);
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(resp.getOutputStream());
            transformer.transform(source, result);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
