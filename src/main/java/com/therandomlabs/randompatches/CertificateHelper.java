package com.therandomlabs.randompatches;

import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;

//Taken from Minecraft Forge and modified
public final class CertificateHelper {
	private static final String HEXES = "0123456789abcdef";

	private CertificateHelper() {}

	public static List<String> getFingerprints(Certificate[] certificates) {
		int length = 0;

		if(certificates != null) {
			length = certificates.length;
		}

		final List<String> fingerprints = new ArrayList<>(length);

		for(int i = 0; i < length; i++) {
			fingerprints.add(CertificateHelper.getFingerprint(certificates[i]));
		}

		return fingerprints;
	}

	public static String getFingerprint(Certificate certificate) {
		if(certificate == null) {
			return "NO VALID CERTIFICATE FOUND";
		}

		try {
			final MessageDigest digest = MessageDigest.getInstance("SHA-1");
			final byte[] encoded = certificate.getEncoded();

			digest.update(encoded);

			final byte[] checksum = digest.digest();
			return hexify(checksum);
		} catch(Exception ex) {
			return "CERTIFICATE FINGERPRINT EXCEPTION";
		}
	}

	private static String hexify(byte[] checksum) {
		final StringBuilder hex = new StringBuilder(2 * checksum.length);

		for(byte b : checksum) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt(b & 0x0F));
		}

		return hex.toString();
	}
}
