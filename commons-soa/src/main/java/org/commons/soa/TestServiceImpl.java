package org.commons.soa;

public class TestServiceImpl implements TestService {

	@Override
	public int test(String msg) {
		
		System.out.println("-----------------"+msg);
		
		return 0;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
