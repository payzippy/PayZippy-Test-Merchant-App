package com.example.testmerc;

import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.payzippy.oneclick.data.Config;
import com.payzippy.oneclick.merchantrequest.ChargingRequest;
import com.payzippy.oneclick.merchantrequest.PayByPayZippy;
import com.payzippy.oneclick.merchantrequest.PayzippyCallback;
import com.payzippy.oneclick.utils.Logger;

public class ChargingForm extends Activity
{
	private static final String TAG = ChargingForm.class.getName();
	protected PayByPayZippy pz;
	String requestParams = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// A sample layout charging_form.xml is created for demo app, the fields are merchant specific.
		setContentView(R.layout.charging_form);
		final EditText merchantTransactionId = (EditText) findViewById(R.id.merchant_transaction_id);
		final EditText buyerEmailAddress = (EditText) findViewById(R.id.buyer_email_address);
		final EditText transactionAmount = (EditText) findViewById(R.id.transaction_amount);
		final Spinner paymentMethod = (Spinner) findViewById(R.id.payment_method);
		final Button submit = (Button) findViewById(R.id.bPay);

		submit.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				try
				{
					// Instance of Charging Request. ChargingRequest.java is in the oneclick-SDK which helps in setting
					// the mandatory and optional parameters and do a validation check
					ChargingRequest cr = new ChargingRequest();

					// Set mandatory parameters. These methods are in ChargingRequest.java.
					// These values can be set once which need not be changed.
					// Below 2 are Merchant Specific values.
					cr.setMerchantId("Enter Your Merchant Id");
					cr.setMerchantKeyId("Enter Your Merchant Key Id");
					cr.setCallbackUrl("Enter your callback url");
					// ****************************************************************************************
					// Irrespective of any merchant. This is fetched from Config.java which is in our SDK
					cr.setUiMode(Config.UI_MODE); // Redirect
					cr.setCurrency(Config.CURRENCY); // INR
					cr.setHashMethod(Config.HASH_METHOD); // SHA-256
					cr.setTransactionType(Config.TRANSACTION_TYPE); // SALE
					cr.setSource(Config.SOURCE); // MOBILE_SDK
					// ****************************************************************************************
					// These values are to be set by merchant based on transactions.
					cr.setMerchantTransactionId(merchantTransactionId.getText().toString());
					cr.setBuyerEmailId(buyerEmailAddress.getText().toString());
					cr.setTransactionAmount(Integer.parseInt(transactionAmount.getText().toString())); // Amount in
					                                                                                   // paise(Integer)
					cr.setPaymentMethod(paymentMethod.getSelectedItem().toString());
					// Bank Name is required only if Net Banking/ EMI is selected. Pass the bank name in the below
					// field.
					if (paymentMethod.getSelectedItem().toString().equalsIgnoreCase("NET"))
						cr.setBankName("");

					if (paymentMethod.getSelectedItem().toString().equalsIgnoreCase("EMI"))
					{
						cr.setBankName("");
						// EMI Months is required if payment method is EMI.
						cr.setEmiMonths("");
					}
					// ****************************************************************************************
					// Optional Parameters, but are used for fraud detection. These are to be set by merchants based on
					// transaction. Sample values are fetched from Config.java.
					cr.setProductInfo1("");
					cr.setProductInfo2("");
					cr.setProductInfo3("");
					cr.setBuyerPhoneNo("");
					cr.setShippingAddress("");
					cr.setShippingCity("");
					cr.setShippingState("");
					cr.setShippingZip("");
					cr.setShippingCountry("");
					cr.setBillingAddress("");
					cr.setBillingCity("");
					cr.setBillingState("");
					cr.setBillingZip("");
					cr.setBillingCountry("");
					cr.setBuyerUniqueId("");
					cr.setItemTotal("");
					cr.setItemVertical("");
					// ****************************************************************************************
					String hashMe = GenerateHash.hash(cr.getRequestParams());
					cr.setHash(hashMe);

					cr.build();

					pz = new PayByPayZippy();
					pz.payZippyPay(ChargingForm.this, cr, payzippyCallBack);

				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}

			}
		});
	}

	/**
	 * This callback interface is implemented by Merchant in his app, so that after payment control can return to
	 * merchant's app. In case of transaction successful or failure, handle it using if-else. In this demo app we have
	 * handled
	 */
	final PayzippyCallback payzippyCallBack = new PayzippyCallback()
	{
		@Override
		public void onPaymentFinished(Map<String, String> responseParams)
		{
			// generate Hash from server after sending all the parameters to the server to check for Hash Mismatch
			// errors. Payment here may or may not be successful.

			// Hash should be generated in your server. Do NOT keep your secret key in you app.
			String calculatedHash = GenerateHash.hash(responseParams);
			if (calculatedHash.equalsIgnoreCase(responseParams.get("hash")))
			{
				// Handle Successful Payment
				if (responseParams.get("transaction_response_code").equalsIgnoreCase("SUCCESS"))
				{
					runOnUiThread(new Runnable()
					{
						public void run()
						{
							// A sample layout on_successful_payment.xml is created for demo app to show success message
							setContentView(R.layout.on_successful_payment);
						}
					});

					Logger.verbose(TAG, "Payment Successful!!!");
				}
				// Handle Payment Failure
				else
				{
					runOnUiThread(new Runnable()
					{
						public void run()
						{
							// A sample layout on_failed_payment.xml is created for demo app to show failure message
							setContentView(R.layout.on_failed_payment);
						}
					});
					Logger.verbose(TAG, "Payment Failed!!!");
				}
			}
			// Handle Hash Mismatch case
			else
			{
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						// A sample layout on_hash_mismatch.xml is created for demo app to show hash-mismatch message
						setContentView(R.layout.on_hash_mismatch);
					}
				});
				Logger.verbose(TAG, "Hash Mismatch!!!");

			}
		}

	};
}
