package mercury.helpers;

import java.util.Base64;

public class EncriptionHelper {

	public static String encrypt(String plain) {
		String b64encoded = Base64.getEncoder().encodeToString(plain.getBytes());

		// Reverse the string
		String reverse = new StringBuffer(b64encoded).reverse().toString();

		StringBuilder sb = new StringBuilder();
		final int OFFSET = 4;
		for (int i = 0; i < reverse.length(); i++) {
			sb.append((char)(reverse.charAt(i) + OFFSET));
		}
		return sb.toString();
	}

	public static String decrypt(String secret) {
		StringBuilder sb = new StringBuilder();
		final int OFFSET = 4;
		for (int i = 0; i < secret.length(); i++) {
			sb.append((char)(secret.charAt(i) - OFFSET));
		}

		String reversed = new StringBuffer(sb.toString()).reverse().toString();
		return new String(Base64.getDecoder().decode(reversed));
	}

}
