import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.*;


/**
 * Класс выполняет веб-сканирование в нескольких потоках
 */
public class CrawlerTask implements Runnable {
	URLPool urlPool;
	private static final String URL_PREFIX = "<a href=\"http";

	/**
	 * Конструктор
	 */
	public CrawlerTask(URLPool pool){
		this.urlPool = pool;
	}

	/**
	 * Метод для составления запроса
	 */
	public static void request(PrintWriter out,URLDepthPair pair) throws MalformedURLException {
		String request = "GET " + pair.getPath() + " HTTP/1.1\r\nHost:" + pair.getHost() + "\r\nConnection: Close\r\n";
		out.println(request);
		// записывает данные с буффера
		out.flush();
	}

	/**
	 * Метод для формирования ссылки
	 */
	public static void buildNewUrl(String str,int depth,URLPool pool){
		try {
			String currentLink = str.substring(str.indexOf(URL_PREFIX)+9,str.indexOf("\"", str.indexOf(URL_PREFIX)+9));
			// добавляем в pool
			pool.addPair(new URLDepthPair(currentLink, depth + 1));
		}
		catch (StringIndexOutOfBoundsException e) {}
	}

	@Override
	public void run(){
		while (true){
			// получаем пару из urlPool
			URLDepthPair currentPair = urlPool.getPair();
			try {
				Socket my_socket;
				try {
					// Создаем новый сокет из полученной строки с именем хоста и номера порта
					my_socket = new Socket(currentPair.getHost(), 80);
				} catch (UnknownHostException e) {
					System.out.println("Could not resolve URL: "+currentPair.getURL()+" at depth "+currentPair.getDepth());
					continue;
				}
				// Устанавливаем время ожидания сокета,
				// чтобы сокет знал, сколько нужно ждать передачи с другой стороны
				my_socket.setSoTimeout(1000);
				try {
					// Вывод текующего сканирования
					System.out.println("Now scanning: "+currentPair.getURL()+" at depth "+currentPair.getDepth());
					// Сокет отправляет даннные на другую сторону соединения
					PrintWriter out = new PrintWriter(my_socket.getOutputStream(), true);
					// Сокет получает даннные с другой стороны соединения
					BufferedReader in = new BufferedReader(new InputStreamReader(my_socket.getInputStream()));
					// Вызываем запрос
					request(out,currentPair);
					String line; // собираем ссылку
					//читаем строку
					while ((line = in.readLine()) != null){
						System.out.println(line);
						if (line.indexOf(currentPair.URL_PREFIX)!=-1){
							// Вызывает метод формирующий ссылку
							buildNewUrl(line,currentPair.getDepth(),urlPool);
						}
					}
					// закрываем сокет
					my_socket.close();
				} catch (SocketTimeoutException e) {
					my_socket.close();
				}
			}
			catch (IOException e) {}
		}
	}
}
