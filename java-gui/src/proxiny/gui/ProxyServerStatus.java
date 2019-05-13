package proxiny.gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ProxyServerStatus {

    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public static String ERROR = "error";

    public static String FATAL = "fatal";

    public static String ERROR_MSG = "error_msg";

    public static String PROXY_STATUS = "proxy_status";

    public static String REQUESTS = "requests";

    public static String RESPONSES = "responses";

    /**
     * This is me
     */
    private static ProxyServerStatus me;

    /**
     * The current state of the proxy server
     */
    private boolean isProxyOn = false;

    /**
     * If there's an isError with the proxy server
     */
    private boolean isError = false;

    /**
     * If there's a fatal error with the proxy so that we need to terminate the GUI
     */
    private boolean isFatal = false;


    /**
     * The number of requests counted so far.
     */
    private int requestsCounter = 0;

    /**
     * The number of responses counted so far.
     */
    private int responsesCounter = 0;

    /**
     * The isError message
     */
    private String errorMessage = "";

    public static ProxyServerStatus getInstance(){
        if ( me == null) {
            me = new ProxyServerStatus();
        }
        return me;
    }


    private ProxyServerStatus(){
        requestsCounter = 0;
        responsesCounter = 0;
    }

    public boolean isProxyOn() {
        return isProxyOn;
    }

    public void setProxyOn(boolean proxyOn) {
        pcs.firePropertyChange(PROXY_STATUS, this.isProxyOn, proxyOn);
        isProxyOn = proxyOn;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        pcs.firePropertyChange(ERROR, this.isError, error);
        this.isError = error;
    }

    public boolean isFatal() {
        return isFatal;
    }

    public void setFatal(boolean isFatal) {
        pcs.firePropertyChange(FATAL, this.isFatal, isFatal);
        this.isFatal = isFatal;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        pcs.firePropertyChange(ERROR, this.errorMessage, errorMessage);
        this.errorMessage = errorMessage;
    }

    public int getRequestsCounter() {
        return requestsCounter;
    }

    public int getResponsessCounter() {
        return responsesCounter;
    }

    public void setRequestsCounter(int requestsCounter) {
        pcs.firePropertyChange(REQUESTS, this.requestsCounter, requestsCounter);
        this.requestsCounter = requestsCounter;
    }

    public void setResponsesCounter(int responsesCounter) {
        pcs.firePropertyChange(RESPONSES, this.responsesCounter, responsesCounter);
        this.responsesCounter = responsesCounter;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

}
