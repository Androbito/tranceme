package com.gnb.web;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Thread to get the result of an web request in format String
 * 
 * @author
 * @version 1.0
 */
public class WebThread extends Thread {

	/**
	 * constant attribute for the post method
	 */
	public static final int METHOD_POST = 1;
	/**
	 * constant attribute for the get method
	 */
	public static final int METHOD_GET = 2;

	/**
	 * different types of encoding
	 */
	public static final String ENCODING_ISO = "ISO-8859-1";
	public static final String ENCODING_UTF_8 = "UTF-8";
	public static final String ENCODING_LATIN = "Latin-1";

	boolean useBase64Encoder;

	/**
	 * the url of the web page
	 */
	String url;
	/**
	 * the content of the web page
	 */
	String resultat;
	/**
	 * request method :Get or Post
	 */
	int methode;
	/**
	 * the listener of the tread
	 */
	WebListener listener;
	/**
	 * parameters to send
	 */
	Map<String, String> params;
	/**
	 * parameters to be sent as JSON Object
	 */
	JSONObject jsonParams;

	/**
	 * Manager of Connection
	 */
	ConnectivityManager connectionManager;

	/**
	 * timeout of the connection
	 */
	int timeoutConnection = WebThreadConstant.TIMEOUT_CONNECT_AD_SERVER * 1000;
	/**
	 * timeout of the Socket
	 */
	int timeoutSocket = WebThreadConstant.TIMEOUT_COMMUNICATION_AD_SERVER * 1000;

	/**
	 * the encoding of the web page
	 */
	String encoding;

	/**
	 * Constructor with 4 parameters
	 * 
	 * @param url
	 *            the url of the web page
	 * @param methode
	 *            method to send request ( Get or Post)
	 * @param params
	 *            parameters to send
	 * @param connectionManager
	 *            Manager of connections
	 */
	public WebThread(String url, int methode,
			Map<String, String> params,
			ConnectivityManager connectionManager, String encoding,
			boolean useBase64Encoder) {
		Log.d("WebThread","url :"+url);
		this.url = url;
		this.methode = methode;
		this.params = params;
		this.connectionManager = connectionManager;
		this.encoding = encoding;
		this.useBase64Encoder = useBase64Encoder;
	}

	/**
	 * Constructor with 4 parameters
	 * 
	 * @param url
	 *            the url of the web page
	 * @param methode
	 *            method to send request ( Get or Post)
	 * @param jsonParams
	 *            parameters to send as a JSONOBJECT
	 * @param connectionManager
	 *            Manager of connections
	 */
	public WebThread(String url, int methode, JSONObject jsonParams,
			ConnectivityManager connectionManager, String encoding,
			boolean useBase64Encoder) {
		Log.d("WebThread","url  :"+url);
		this.url = url;
		this.methode = methode;
		this.jsonParams = jsonParams;
		this.connectionManager = connectionManager;
		this.encoding = encoding;
		this.useBase64Encoder = useBase64Encoder;
	}

	/**
	 * Constructor when we don't need to send any parameter
	 * 
	 * @param url
	 * @param methode
	 * @param connectionManager
	 */

	public WebThread(String url, int methode,
			ConnectivityManager connectionManager, String encoding,
			boolean useBase64Encoder) {
		this.url = url;
		this.methode = methode;
		this.connectionManager = connectionManager;
		this.encoding = encoding;
		this.params = null;
		this.useBase64Encoder = useBase64Encoder;
	}

	/**
	 * method run() :what the WebThread will do
	 */
	@Override
	public void run() {

		HttpClient client;
		HttpResponse reponse = null;

		try {
			// check if an Internet connection is available
			if (connexionStatus(connectionManager) == true) {
				//

				// define the parameter TIMEOUT of the Connection
				HttpParams httpParameters = new BasicHttpParams();

				// the connection timeout in milliseconds
				HttpConnectionParams.setConnectionTimeout(httpParameters,
						timeoutConnection);

				// the socket timeout (SO_TIMEOUT) in milliseconds (timeout for
				// waiting for data)
				HttpConnectionParams
						.setSoTimeout(httpParameters, timeoutSocket);

				HttpProtocolParams.setContentCharset(httpParameters, "UTF-8");

				// traitement url sécurisé
				if (url.startsWith("https")) {

					HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
					SchemeRegistry registry = new SchemeRegistry();
					SSLSocketFactory socketFactory = SSLSocketFactory
							.getSocketFactory();
					socketFactory
							.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
					registry.register(new Scheme("https", socketFactory, 443));
					SingleClientConnManager mgr = new SingleClientConnManager(
							httpParameters, registry);
					client = new DefaultHttpClient(mgr, httpParameters);
				} else {
					client = new DefaultHttpClient(httpParameters);
				}

				switch (this.methode) {
				// treatment for POST method
				case METHOD_POST:
					if (jsonParams != null)
						reponse = methodePost(client, url, jsonParams);
					else
						reponse = methodePost(client, url, params);
					break;

				// treatment for GET method
				case METHOD_GET:
					if (jsonParams != null)
						reponse = methodeGet(client, url, jsonParams);
					else
						reponse = methodeGet(client, url, params);
					break;
				}

				// Recover the result of the web request
				//this.resultat = getPage(reponse.getEntity().getContent(),
					//	this.encoding);
				this.resultat= EntityUtils.toString(reponse.getEntity());
				getListener().onFinish(this.url, this.resultat);
			} else {
				throw WebException
						.getException(WebException.CODE_EXCEPTION_NO_CONNECTION);// R.string.titleErrorAlert,R.string.ConnectionException);
			}
		} catch (WebException e) {
			getListener().onError(e);
		} catch (IOException e) {
			getListener()
					.onError(
							WebException
									.getException(WebException.CODE_EXCEPTION_RECEIVING_DATA));// R.string.titleErrorAlert,R.string.ErrorReceivingException));
		} catch (IllegalArgumentException e) {
			getListener()
					.onError(
							WebException
									.getException(WebException.CODE_EXCEPTION_URL_SYNTAX));
		} catch (Exception e) {
			getListener()
					.onError(
							WebException
									.getException(WebException.CODE_EXCEPTION_NO_CONNECTION));
		}
	}

	/**
	 * method to recover the content of the web page
	 * 
	 * @param data
	 *            the InputStream recovered from the web request
	 * @return return the content of the web page in String format
	 * @throws WebException
	 */
	private String getPage(InputStream data, String encoding)
			throws WebException {
		StringBuffer strBuffer = new StringBuffer("");
		BufferedReader bfReader = null;
		try {
			bfReader = new BufferedReader(new InputStreamReader(data, encoding));
		} catch (UnsupportedEncodingException e1) {
			throw WebException
					.getException(WebException.CODE_EXCEPTION_UNSUPPORTED_ENCODING);// ,R.string.titleErrorAlert,R.string.UnsupportedEncodingException);
		}

		String ligne;
		try {
			while ((ligne = bfReader.readLine()) != null) {
				strBuffer.append(ligne);
				strBuffer.append("\n");
			}
		} catch (IOException e) {
			throw WebException
					.getException(WebException.CODE_EXCEPTION_RECEIVING_DATA);// ,R.string.titleErrorAlert,R.string.ErrorReceivingException);
		}

		return strBuffer.toString();
	}

	/**
	 * treatment for POST method
	 * 
	 * @param client
	 * @see HttpClient
	 * @param url
	 * @param params
	 * @return {@link HttpResponse}
	 * @throws WebException
	 */
	private HttpResponse methodePost(HttpClient client, String url,
			Map<String, String> params) throws WebException {
		HttpPost httpPOST = new HttpPost(url);

		// remplissage des param�tres � partir du Map params
		if (params != null) {
			List<NameValuePair> parametres = new ArrayList<NameValuePair>();
			Iterator<Entry<String, String>> i = params.entrySet().iterator();
			while (i.hasNext()) {
				Entry<String, String> element = i.next();
				parametres.add(new BasicNameValuePair(element.getKey(), element.getValue()));
				Log.d("WebThread", "<" + element.getKey() + " | " + element.getValue() + ">");
			}

			// encapsulation of parameters into the httpPost
			UrlEncodedFormEntity formEntity = null;
			try {
				formEntity = new UrlEncodedFormEntity(parametres);
			} catch (UnsupportedEncodingException e) {
				Log.i("UnsupportedEncodingException","WebThread");
				throw WebException.getException(WebException.CODE_EXCEPTION_UNSUPPORTED_ENCODING);// ,R.string.titleErrorAlert,R.string.UnsupportedEncodingException);
			}
			httpPOST.setEntity(formEntity);
		}
		// execute the web request
		HttpResponse reponse = null;
		try {
			reponse = client.execute(httpPOST);
		} catch (IOException e) {
			e.printStackTrace();
			Log.i("probleme","WebThread");
			throw WebException.getException(WebException.CODE_EXCEPTION_TIMEOUT);// ,R.string.titleErrorAlert,R.string.TimeoutException);
		}
		return reponse;
	}

	/**
	 * treatment for POST method
	 * 
	 * @param client
	 * @see HttpClient
	 * @param url
	 * @param jsonParams
	 * @return {@link HttpResponse}
	 * @throws WebException
	 */
	private HttpResponse methodePost(HttpClient client, String url,
			JSONObject jsonParams) throws WebException {
		HttpPost methodePOST = new HttpPost(url);

		// filling parameters from the object JSONObject into a StringEntity
		if (jsonParams != null) {
			StringEntity stringEntity;
			try {
				String header = jsonParams.toString();
				if (useBase64Encoder){
					header = Base64Coder.encodeString(header);
				}
				Log.d("NXM","header >> " + header);
				// Log.i("NXM", "header http : " + header);
				stringEntity = new StringEntity(header);
				stringEntity.setContentEncoding("UTF-8");
				stringEntity.setContentEncoding(new BasicHeader(
						HTTP.CONTENT_TYPE, "application/json"));
				// stringEntity = new StringEntity( s );
			} catch (UnsupportedEncodingException e) {
				throw WebException
						.getException(WebException.CODE_EXCEPTION_UNSUPPORTED_ENCODING);// ,R.string.titleErrorAlert,R.string.UnsupportedEncodingException);
			}
			// List<NameValuePair> parametres = new ArrayList<NameValuePair>();
			// parametres.add(new
			// BasicNameValuePair("json",_jsonParams.toString()));
			//
			// UrlEncodedFormEntity formEntity = null;
			// try {
			// formEntity = new UrlEncodedFormEntity(parametres);
			// }catch (UnsupportedEncodingException e){
			// throw
			// WebException.getException(WebException.CODE_EXCEPTION_UNSUPPORTED_ENCODING,R.string.titleErrorAlert,R.string.UnsupportedEncodingException);
			// }

			// encapsulation of parameters into the httpPost
			methodePOST.setEntity(stringEntity);
		}
		// execute the web request
		HttpResponse reponse = null;
		try {
			reponse = client.execute(methodePOST);
		} catch (IOException e) {
			throw WebException
					.getException(WebException.CODE_EXCEPTION_TIMEOUT);// ,R.string.titleErrorAlert,R.string.TimeoutException);
		}
		return reponse;
	}

	/**
	 * treatment for GET method
	 * 
	 * @param _client
	 * @param _url
	 * @param _params
	 * @return {@link HttpResponse}
	 * @throws WebException
	 */
	private HttpResponse methodeGet(HttpClient client, String url,
			Map<String, String> params) throws WebException {
		HttpGet httpGET = new HttpGet();
		StringBuffer urlBuffer = new StringBuffer(url);

		// add parameters to the URL
		if (params != null) {
			urlBuffer.append("?");
			Iterator<Entry<String, String>> i = params.entrySet().iterator();
			int cmp = 0;
			while (i.hasNext()) {
				Entry<String, String> element = i.next();
				cmp++;
				// if this is the last element, we don't concat "&"
				if (cmp == params.size())
					urlBuffer.append(element.getKey() + "="
							+ element.getValue());
				else
					urlBuffer.append(element.getKey() + "="
							+ element.getValue() + "&");
			}
		}

		// setting properties of the web request
		URI uri = null;
		try {
			uri = new URI(urlBuffer.toString());
			httpGET.setURI(uri);
		} catch (URISyntaxException e) {
			throw WebException
					.getException(WebException.CODE_EXCEPTION_URL_SYNTAX);// ,R.string.titleErrorAlert,R.string.URLSyntaxException);
		}

		// execute the request
		try {
			return client.execute(httpGET);
		} catch (IOException e) {
			throw WebException
					.getException(WebException.CODE_EXCEPTION_TIMEOUT);// ,R.string.titleErrorAlert,R.string.TimeoutException);
		}
	}

	/**
	 * treatment for GET method
	 * 
	 * @param client
	 * @param url
	 * @param jsonParams
	 * @return {@link HttpResponse}
	 * @throws WebException
	 */
	private HttpResponse methodeGet(HttpClient client, String url,
			JSONObject jsonParams) throws WebException {
		HttpGet httpGET = new HttpGet();
		StringBuffer urlBuffer = new StringBuffer(url);

		// add parameters to the URL
		if (jsonParams != null) {
			urlBuffer.append("?");
			int cmp = 0;
			String key;
			for (Iterator<?> i = jsonParams.keys(); i.hasNext();) {
				cmp++;
				key = (String) i.next();
				// if this is the last element, we don't concat "&"
				try {
					if (cmp == jsonParams.length())
						urlBuffer.append(key + "="
								+ jsonParams.get(String.valueOf(key)));
					else
						urlBuffer.append(key + "="
								+ jsonParams.get(String.valueOf(key)) + "&");
				} catch (JSONException e) {
					throw WebException
							.getException(WebException.CODE_EXCEPTION_WRONG_FORMAT_JSON);// ,R.string.titleErrorAlert,R.string.FormatJSONException);
				}
			}
			// Log.i("url apres json", urlBuffer.toString());
		}

		// setting properties of the web request
		URI uri = null;
		try {
			uri = new URI(urlBuffer.toString());
			httpGET.setURI(uri);
		} catch (URISyntaxException e) {
			throw WebException
					.getException(WebException.CODE_EXCEPTION_URL_SYNTAX);// ,R.string.titleErrorAlert,R.string.URLSyntaxException);
		}

		// execute the request
		try {
			return client.execute(httpGET);
		} catch (IOException e) {
			throw WebException
					.getException(WebException.CODE_EXCEPTION_TIMEOUT);// ,R.string.titleErrorAlert,R.string.TimeoutException);
		}
	}

	/**
	 * check if an Internet connection is available
	 * 
	 * @param connec
	 * @return return true if there is an Internet Connection available
	 */
	public boolean connexionStatus(ConnectivityManager connec) {
		NetworkInfo[] allNetwork = connec.getAllNetworkInfo();
		if (allNetwork != null) {
			for (int i = 0; i < allNetwork.length; i++) {
				if (allNetwork[i].getState() == NetworkInfo.State.CONNECTED
						|| allNetwork[i].getState() == NetworkInfo.State.CONNECTING)
					return true;
			}
		}
		return false;
	}

	/**
	 * get the listener of the thread
	 * 
	 * @return
	 */
	public WebListener getListener() {
		return listener;
	}

	/**
	 * set the listener of the thread
	 * 
	 * @param listener
	 */
	public void setListener(WebListener listener) {
		this.listener = listener;
	}

	/**
	 * get the result
	 * 
	 * @return
	 */
	public String getResultat() {
		return resultat;
	}

	/**
	 * set the connection timeout
	 * 
	 * @param timeoutConnection
	 */
	public void setTimeoutConnection(int timeoutConnection) {
		this.timeoutConnection = timeoutConnection;
	}

	/**
	 * set the socket timeout
	 * 
	 * @param timeoutSocket
	 */
	public void setTimeoutSocket(int timeoutSocket) {
		this.timeoutSocket = timeoutSocket;
	}
	
	public static String connectSingle(String url) throws ClientProtocolException, IOException
    {
        HttpClient httpclient = new DefaultHttpClient();
        String result = null;
        // Prepare a request object
        HttpGet httpget = new HttpGet(url); 

        // Execute the request
        HttpResponse response;
        
            response = httpclient.execute(httpget);

            // Get hold of the response entity
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            // to worry about connection release

            if (entity != null) 
            {

                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                result= convertStreamToString(instream);
                instream.close();
            }
			return result;
        }

	private static String convertStreamToString(InputStream is) {
	    ByteArrayOutputStream oas = new ByteArrayOutputStream();
	    copyStream(is, oas);
	    String t = oas.toString();
	    try {
	        oas.close();
	        oas = null;
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    return t;
	}

	private static void copyStream(InputStream is, OutputStream os)
	{
	    final int buffer_size = 1024;
	    try
	    {
	        byte[] bytes=new byte[buffer_size];
	        for(;;)
	        {
	          int count=is.read(bytes, 0, buffer_size);
	          if(count==-1)
	              break;
	          os.write(bytes, 0, count);
	        }
	    }
	    catch(Exception ex){}
	}
}
