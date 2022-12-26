import java.util.LinkedList;


/*
Расширили сканер для использования поточной обработки так, чтобы веб-страницы можно было сканировать параллельно
 */
public class Crawler {

	/**
	 * Метод который выводит результат найденных ссылок
	 * @param resultLink
	 */
	public static void showResult(LinkedList<URLDepthPair> resultLink) {
		for (URLDepthPair c : resultLink)
			System.out.println("Depth :" + c.getDepth()+"\tLink :"+c.getURL());
	}

	public static void main(String[] args) {
		try {
			String lineUrl = "http://crawler-test.com/";
			int numThreads = Integer.parseInt("2"); // количество потоков
			URLPool pool = new URLPool(Integer.parseInt("4")); // Заводим пул и закладываем первую пару в него
			pool.addPair(new URLDepthPair(lineUrl, 0));
			// Запуск потоков
			for (int i = 0; i < numThreads; i++) {
				CrawlerTask c = new CrawlerTask(pool);
				Thread t = new Thread(c);
				t.start();
			}
			/**
			 * Отслеживание количество потоков
			 * количество ожидающих потоков != количество потоков
			 */
			while (pool.getWait() != numThreads) {
				try {
					Thread.sleep(500); // приостанавливаем работу потока
				} catch (InterruptedException e) {
					System.out.println("InterruptedException is ignored");
				}
			}
			try {
				// вывод результат
				showResult(pool.getResult());;
			} catch (NullPointerException e) {
				System.out.println("Error! "+e);
			}
			System.exit(0);
		} catch(Exception err) {
			System.out.println("Error!\n"+err);
			System.out.println("Usage: java crawler <site> <depth> <threads>");
		}
	}

}
