package httpflux;

import com.amiano4.httpflux.HttpService;

public class Test {
	public static void main(String[] args) {

		try {
			System.out.println("Test case 1: ");

			HttpService.post("https://httpbin.org/post").onSuccess((response) -> {
				System.out.print(response.body());
			}).onError((response) -> {
				System.out.print("error : " + response.getMessage());
			}).executeSync();

			System.out.println("Test case 2: ");

			HttpService.get("https://httpbin.org/post").onSuccess((response) -> {
				System.out.println(response.body());
			}).onError((error) -> {
				// System.out.println(error.getResponse().body());
				error.printStackTrace();
			}).executeAsync();

			Thread.sleep(3000);

			System.out.println("\nTest end ");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
