package com.example.testmerc;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.payzippy.oneclick.utils.Logger;

public class GenerateHash
{
	private static final String TAG = GenerateHash.class.getName();

	/**
	 * This is just for testing purpose. DO NOT CALL THIS CLASS METHODS FOR GENERATING HASH.
	 * Instead call your server to generate hash. This is just a sample way to generate hash.
	 * Pass Map of charging request params in generateHash function to generate Hash and return back hash.
	 * @param requestParams
	 * @return
	 */
	public static String hash(Map<String, String> requestParams)
	{
		// DO NOT CALL THIS CLASS METHODS FOR GENERATING HASH.
		// Instead call your server to generate hash. This is just a sample way to generate hash.

		Logger.verbose(TAG, "Generating Hash");
		List<String> chargingParams = new ArrayList<String>(requestParams.keySet());
		Collections.sort(chargingParams);
		StringBuilder strBuilder = new StringBuilder(1000);
		for (String s : chargingParams)
		{
			String value = String.valueOf(requestParams.get(s));
			if (value != null && !"hash".equals(s))
				strBuilder.append(value + "|");
		}

		// Do Not keep your secret Key here. Call your server side api where Secret key is there and generate hash there
		// itself.
		strBuilder.append("SECRET_KEY");
		String base = strBuilder.toString();

		try
		{
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(base.getBytes());
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++)
			{
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();
		}
		catch (Exception ex)
		{
			Logger.verbose(TAG, "Problem in Hashing");
			throw new RuntimeException(ex);
		}
	}
}
