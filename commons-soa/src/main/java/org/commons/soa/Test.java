package org.commons.soa;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestService s = CustomerClientFactory.getHolder().getService(TestService.class, "test");

		long start = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {

			int rt = s.test("1111111111");
			System.out.println("***************" + rt);
		}
		long end = System.currentTimeMillis();

		System.out.println("total : " + (end - start));

	}
}
