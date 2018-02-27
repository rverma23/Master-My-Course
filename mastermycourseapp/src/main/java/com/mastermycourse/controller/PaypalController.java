package com.mastermycourse.controller;

import com.mastermycourse.beans.CourseBean;
import com.mastermycourse.paypal.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import urn.ebay.api.PayPalAPI.*;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;
import urn.ebay.apis.eBLBaseComponents.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Author: James DeCarlo.
 */

@Controller
public class PaypalController {

    @RequestMapping(value = "/BuyTextToSpeech", method = RequestMethod.POST)
    public void processOneTimePayment(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        HttpSession session = request.getSession();
        session.setAttribute("url", request.getRequestURI());
        response.setContentType("text/html");

        // Configuration map containing signature credentials and other required configuration.
        // For a full list of configuration parameters refer in wiki page.
        // (https://github.com/paypal/sdk-core-java/blob/master/README.md)
        Map<String,String> configurationMap =  Configuration.getAcctAndConfig();

        // Creating service wrapper object to make an API call by loading configuration map.
        PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(configurationMap);

        //# SetExpressCheckout API
        // The SetExpressCheckout API operation initiates an Express Checkout
        // transaction.
        // This sample code uses Merchant Java SDK to make API call. You can
        // download the SDKs [here](https://github.com/paypal/sdk-packages/tree/gh-pages/merchant-sdk/java)

        SetExpressCheckoutRequestType setExpressCheckoutReq = new SetExpressCheckoutRequestType();
        SetExpressCheckoutRequestDetailsType details = new SetExpressCheckoutRequestDetailsType();

        StringBuffer url = new StringBuffer();
        url.append("http://");
        url.append(request.getServerName());
        url.append(":");
        url.append(request.getServerPort());
        url.append(request.getContextPath());

        String returnURL = url.toString() + "/DoExpressCheckout";
        String cancelURL = url.toString() + "/courseCreation.htm";




        /*
         *  (Required) URL to which the buyer's browser is returned after choosing
         *  to pay with PayPal. For digital goods, you must add JavaScript to this
         *  page to close the in-context experience.
          Note:
            PayPal recommends that the value be the final review page on which the buyer
            confirms the order and payment or billing agreement.
            Character length and limitations: 2048 single-byte characters
         */
        details.setReturnURL(returnURL + "?currencyCodeType=USD");

        details.setCancelURL(cancelURL);
			/*
			 *  (Optional) Email address of the buyer as entered during checkout.
			 *  PayPal uses this value to pre-fill the PayPal membership sign-up portion on the PayPal pages.
			 *	Character length and limitations: 127 single-byte alphanumeric characters
			 */
       // details.setBuyerEmail(session.getAttribute("userEmail").toString());

        double itemTotal = 0.00;
        double orderTotal = 0.00;
        // populate line item details
        //Cost of item. This field is required when you pass a value for ItemCategory.
        String amountItems = ((CourseBean)session.getAttribute("course")).getTextToSpeechPriceString();
			/*
			 * Item quantity. This field is required when you pass a value for ItemCategory.
			 * For digital goods (ItemCategory=Digital), this field is required.
			   Character length and limitations: Any positive integer
			   This field is introduced in version 53.0.
			 */
        String qtyItems = "1";
			/*
			 * Item name. This field is required when you pass a value for ItemCategory.
				Character length and limitations: 127 single-byte characters
				This field is introduced in version 53.0.
			 */
        String names = "Generate Text to Speech" ;

        List<PaymentDetailsItemType> lineItems = new ArrayList<PaymentDetailsItemType>();

        PaymentDetailsItemType item = new PaymentDetailsItemType();
        BasicAmountType amt = new BasicAmountType();
        //PayPal uses 3-character ISO-4217 codes for specifying currencies in fields and variables.
        amt.setCurrencyID(CurrencyCodeType.USD);
        amt.setValue(amountItems);
        item.setQuantity(new Integer(qtyItems));
        item.setName(names);
        item.setAmount(amt);
			/*
			 * Indicates whether an item is digital or physical. For digital goods, this field is required and must be set to Digital. It is one of the following values:
			 	1.Digital
				2.Physical
			   This field is available since version 65.1.
			 */
        item.setItemCategory(ItemCategoryType.fromValue("Digital"));
			/*
			 *  (Optional) Item description.
				Character length and limitations: 127 single-byte characters
				This field is introduced in version 53.0.
			 */
        item.setDescription(((CourseBean)session.getAttribute("course")).getCourseName());
        lineItems.add(item);


        itemTotal += Double.parseDouble(qtyItems) * Double.parseDouble(amountItems);
        orderTotal += itemTotal;

        List<PaymentDetailsType> payDetails = new ArrayList<PaymentDetailsType>();
        PaymentDetailsType paydtl = new PaymentDetailsType();
			/*
			 * How you want to obtain payment. When implementing parallel payments,
			 * this field is required and must be set to Order.
			 *  When implementing digital goods, this field is required and must be set to Sale.
			 *   If the transaction does not include a one-time purchase, this field is ignored.
			 *   It is one of the following values:
				Sale  This is a final sale for which you are requesting payment (default).
				Authorization  This payment is a basic authorization subject to settlement with PayPal Authorization and Capture.
				Order  This payment is an order authorization subject to settlement with PayPal Authorization and Capture.
			 */
        paydtl.setPaymentAction(PaymentActionCodeType.fromValue("Sale"));

        /*
         *  (Optional) Description of items the buyer is purchasing.
             Note:
             The value you specify is available only if the transaction includes a purchase.
             This field is ignored if you set up a billing agreement for a recurring payment
             that is not immediately charged.
             Character length and limitations: 127 single-byte alphanumeric characters
         */

        paydtl.setOrderDescription("Generate Text to Speech");



        BasicAmountType itemsTotal = new BasicAmountType();
        itemsTotal.setValue(Double.toString(itemTotal));
        //PayPal uses 3-character ISO-4217 codes for specifying currencies in fields and variables.
        itemsTotal.setCurrencyID(CurrencyCodeType.USD);
        paydtl.setOrderTotal(new BasicAmountType(CurrencyCodeType.USD,
                Double.toString(orderTotal)));
        paydtl.setPaymentDetailsItem(lineItems);

        paydtl.setItemTotal(itemsTotal);
			/*
			 *  (Optional) Your URL for receiving Instant Payment Notification (IPN)
			 *  about this transaction. If you do not specify this value in the request,
			 *  the notification URL from your Merchant Profile is used, if one exists.
				Important:
				The notify URL applies only to DoExpressCheckoutPayment.
				This value is ignored when set in SetExpressCheckout or GetExpressCheckoutDetails.
				Character length and limitations: 2,048 single-byte alphanumeric characters
			 */
        paydtl.setNotifyURL(null);
        payDetails.add(paydtl);
        details.setPaymentDetails(payDetails);

        setExpressCheckoutReq.setSetExpressCheckoutRequestDetails(details);

        SetExpressCheckoutReq expressCheckoutReq = new SetExpressCheckoutReq();
        expressCheckoutReq.setSetExpressCheckoutRequest(setExpressCheckoutReq);
        SetExpressCheckoutResponseType setExpressCheckoutResponse = null;
        try{
            setExpressCheckoutResponse = service.setExpressCheckout(expressCheckoutReq);
        }catch(Exception e){
            e.printStackTrace();
        }

        if (setExpressCheckoutResponse != null) {
            session.setAttribute("lastReq", service.getLastRequest());
            session.setAttribute("lastResp", service.getLastResponse());
            if (setExpressCheckoutResponse.getAck().toString().equalsIgnoreCase("SUCCESS")) {
                response.sendRedirect("https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token="+setExpressCheckoutResponse.getToken());
            } else {

                session.setAttribute("Error", setExpressCheckoutResponse.getErrors());
                response.sendRedirect("paymentError.htm");
            }
        }
    }

    @RequestMapping(value = "/DoExpressCheckout", method = RequestMethod.GET)
    public void confrimPayment(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        session.setAttribute("url", request.getRequestURI());
        response.setContentType("text/html");

        // Configuration map containing signature credentials and other required configuration.
        // For a full list of configuration parameters refer in wiki page.
        // (https://github.com/paypal/sdk-core-java/blob/master/README.md)
        Map<String,String> configurationMap =  Configuration.getAcctAndConfig();

        // Creating service wrapper object to make an API call by loading configuration map.
        PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(configurationMap);

        DoExpressCheckoutPaymentRequestType doCheckoutPaymentRequestType = new DoExpressCheckoutPaymentRequestType();
        DoExpressCheckoutPaymentRequestDetailsType details = new DoExpressCheckoutPaymentRequestDetailsType();
			/*
			 * A timestamped token by which you identify to PayPal that you are processing
			 * this payment with Express Checkout. The token expires after three hours.
			 * If you set the token in the SetExpressCheckout request, the value of the token
			 * in the response is identical to the value in the request.
			   Character length and limitations: 20 single-byte characters
			 */
        details.setToken(request.getParameter("token"));
			/*
			 * Unique PayPal Customer Account identification number.
			   Character length and limitations: 13 single-byte alphanumeric characters
			 */
         if(request.getParameter("PayerID") == null) throw new Exception("no payerID");
        details.setPayerID(request.getParameter("PayerID"));
			/*
			 *  (Optional) How you want to obtain payment. If the transaction does not include
			 *  a one-time purchase, this field is ignored.
			 *  It is one of the following values:
					Sale  This is a final sale for which you are requesting payment (default).
					Authorization  This payment is a basic authorization subject to settlement with PayPal Authorization and Capture.
					Order  This payment is an order authorization subject to settlement with PayPal Authorization and Capture.
				Note:
				You cannot set this field to Sale in SetExpressCheckout request and then change
				this value to Authorization or Order in the DoExpressCheckoutPayment request.
				If you set the field to Authorization or Order in SetExpressCheckout,
				you may set the field to Sale.
				Character length and limitations: Up to 13 single-byte alphabetic characters
				This field is deprecated. Use PaymentAction in PaymentDetailsType instead.
			 */
        details.setPaymentAction(PaymentActionCodeType.fromValue("Sale"));
        double itemTotalAmt = 0.00;
        double orderTotalAmt = 0.00;
        String amt = ((CourseBean)session.getAttribute("course")).getTextToSpeechPriceString();
        String quantity = "1";
        itemTotalAmt = Double.parseDouble(amt) * Double.parseDouble(quantity);
        orderTotalAmt += itemTotalAmt;

        PaymentDetailsType paymentDetails = new PaymentDetailsType();
        BasicAmountType orderTotal = new BasicAmountType();
        orderTotal.setValue(Double.toString(orderTotalAmt));
        //PayPal uses 3-character ISO-4217 codes for specifying currencies in fields and variables.
        orderTotal.setCurrencyID(CurrencyCodeType.USD);
			/*
			 *  (Required) The total cost of the transaction to the buyer.
			 *  If shipping cost (not applicable to digital goods) and tax charges are known,
			 *  include them in this value. If not, this value should be the current sub-total
			 *  of the order. If the transaction includes one or more one-time purchases, this
			 *  field must be equal to the sum of the purchases. Set this field to 0 if the
			 *  transaction does not include a one-time purchase such as when you set up a
			 *  billing agreement for a recurring payment that is not immediately charged.
			 *  When the field is set to 0, purchase-specific fields are ignored.
			 *  For digital goods, the following must be true:
				total cost > 0
				total cost <= total cost passed in the call to SetExpressCheckout
			 Note:
				You must set the currencyID attribute to one of the 3-character currency codes
				for any of the supported PayPal currencies.
				When multiple payments are passed in one transaction, all of the payments must
				have the same currency code.
				Character length and limitations: Value is a positive number which cannot
				exceed $10,000 USD in any currency. It includes no currency symbol.
				It must have 2 decimal places, the decimal separator must be a period (.),
				and the optional thousands separator must be a comma (,).
			 */
        paymentDetails.setOrderTotal(orderTotal);

        BasicAmountType itemTotal = new BasicAmountType();
        itemTotal.setValue(Double.toString(itemTotalAmt));
        //PayPal uses 3-character ISO-4217 codes for specifying currencies in fields and variables.
        itemTotal.setCurrencyID(CurrencyCodeType.USD);
			/*
			 *  Sum of cost of all items in this order. For digital goods, this field is
			 *  required. PayPal recommends that you pass the same value in the call to
			 *  DoExpressCheckoutPayment that you passed in the call to SetExpressCheckout.
			 Note:
				You must set the currencyID attribute to one of the 3-character currency
				codes for any of the supported PayPal currencies.
				Character length and limitations: Value is a positive number which cannot
				exceed $10,000 USD in any currency. It includes no currency symbol.
				It must have 2 decimal places, the decimal separator must be a period (.),
				and the optional thousands separator must be a comma (,).
			 */
        paymentDetails.setItemTotal(itemTotal);

        List<PaymentDetailsItemType> paymentItems = new ArrayList<PaymentDetailsItemType>();
        PaymentDetailsItemType paymentItem = new PaymentDetailsItemType();
			/*
			 * Item name. This field is required when you pass a value for ItemCategory.
			   Character length and limitations: 127 single-byte characters
			   This field is introduced in version 53.0.
			 */
        paymentItem.setName(((CourseBean)session.getAttribute("course")).getCourseName());
			/*
			 * Item quantity. This field is required when you pass a value for ItemCategory.
			 * For digital goods (ItemCategory=Digital), this field is required.
				Character length and limitations: Any positive integer
				This field is introduced in version 53.0.
			 */
        paymentItem.setQuantity(1);
        BasicAmountType amount = new BasicAmountType();
			/*
			 * Cost of item. This field is required when you pass a value for ItemCategory.
			Note:
			You must set the currencyID attribute to one of the 3-character currency codes for
			any of the supported PayPal currencies.
			Character length and limitations: Value is a positive number which cannot
			exceed $10,000 USD in any currency. It includes no currency symbol.
			It must have 2 decimal places, the decimal separator must be a period (.),
			and the optional thousands separator must be a comma (,).
			This field is introduced in version 53.0.
			 */
        amount.setValue(((CourseBean)session.getAttribute("course")).getTextToSpeechPriceString());
        //PayPal uses 3-character ISO-4217 codes for specifying currencies in fields and variables.
        amount.setCurrencyID(CurrencyCodeType.USD);
        paymentItem.setAmount(amount);
        paymentItems.add(paymentItem);
        paymentDetails.setPaymentDetailsItem(paymentItems);
			/*
			 *  (Optional) Your URL for receiving Instant Payment Notification (IPN)
			 *  about this transaction. If you do not specify this value in the request,
			 *  the notification URL from your Merchant Profile is used, if one exists.
				Important:
				The notify URL applies only to DoExpressCheckoutPayment.
				This value is ignored when set in SetExpressCheckout or GetExpressCheckoutDetails.
				Character length and limitations: 2,048 single-byte alphanumeric characters
			 */
        paymentDetails.setNotifyURL(null);

        List<PaymentDetailsType> payDetailType = new ArrayList<PaymentDetailsType>();
        payDetailType.add(paymentDetails);
			/*
			 * When implementing parallel payments, you can create up to 10 sets of payment
			 * details type parameter fields, each representing one payment you are hosting
			 * on your marketplace.
			 */
        details.setPaymentDetails(payDetailType);

        doCheckoutPaymentRequestType
                .setDoExpressCheckoutPaymentRequestDetails(details);
        DoExpressCheckoutPaymentReq doExpressCheckoutPaymentReq = new DoExpressCheckoutPaymentReq();
        doExpressCheckoutPaymentReq.setDoExpressCheckoutPaymentRequest(doCheckoutPaymentRequestType);
        DoExpressCheckoutPaymentResponseType doCheckoutPaymentResponseType = null;
        try{
            doCheckoutPaymentResponseType = service.doExpressCheckoutPayment(doExpressCheckoutPaymentReq);

        }catch(Exception e){
            e.printStackTrace();
        }
        response.setContentType("text/html");

        if (doCheckoutPaymentResponseType != null) {
            session.setAttribute(
                    "nextDescription",
                    "<ul>If  paymentAction is <b>Authorization</b> .you can capture the payment directly using DoCapture api" +
                            " <li><a href='AuthorizedPaymentCapture'>DoCapture</a></li>" +
                            "If  paymentAction is <b>Order</b> .you need to call DoAuthorization api, before you can capture the payment using DoCapture api." +
                            "<li><a href='DoAuthorizationForOrderPayment'>DoAuthorization</a></li></ul>");
            session.setAttribute("lastReq", service.getLastRequest());
            session.setAttribute("lastResp", service.getLastResponse());
            if (doCheckoutPaymentResponseType.getAck().toString()
                    .equalsIgnoreCase("SUCCESS")) {
                Map<Object, Object> map = new LinkedHashMap<Object, Object>();
                map.put("Ack", doCheckoutPaymentResponseType.getAck());
                Iterator<PaymentInfoType> iterator = doCheckoutPaymentResponseType
                        .getDoExpressCheckoutPaymentResponseDetails()
                        .getPaymentInfo().iterator();
                int index = 1;
					/*
					 * Unique transaction ID of the payment.
					 Note:
						If the PaymentAction of the request was Authorization or Order,
						this value is your AuthorizationID for use with the Authorization
						& Capture APIs.
						Character length and limitations: 19 single-byte characters
					 */
                while (iterator.hasNext()) {
                    PaymentInfoType result = (PaymentInfoType) iterator.next();
                    map.put("Transaction ID" + index,
                            result.getTransactionID());
                    index++;
                }
                session.setAttribute("transactionId", doCheckoutPaymentResponseType.getDoExpressCheckoutPaymentResponseDetails().getPaymentInfo().get(0).getTransactionID());
                session.setAttribute("map", map);
                response.sendRedirect("/ParseTextToSpeech");
            } else {

                session.setAttribute("Error",
                        doCheckoutPaymentResponseType.getErrors());
                response.sendRedirect("/paymentError.htm");
            }
        }

    }
}
