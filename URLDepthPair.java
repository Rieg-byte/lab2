import java.util.LinkedList;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Класс для представления пар ссылка, глубина для нашего искателя.
 */
public class URLDepthPair {
	/**
	 * Поля для представления текущего константы, URL-адреса и текущей глубины.
	 */
	public static final String URL_PREFIX = "<a href=\"http";
	public String URL;
	private int depth;
	URL host_path;
	/**
	 * Конструктор, который устанавливает входные данные на текущий URL-адрес и глубину.
	 */
	public URLDepthPair (String URL, int depth){
		this.URL=URL;
		this.depth=depth;
		// Исключение
		try {
			this.host_path= new URL(URL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
 	* Метод, который возвращает веб-хост текущего URL-адреса.
    */
	public String getHost(){
		return host_path.getHost();
	}

	/**
	 * Метод для получения пути
	 */
	public String getPath(){
		return host_path.getPath();
	}

	/**
	 * Метод, который возвращает текущую глубину.
	 * @return
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 *  Метод, который возвращает полный URL-адрес.
	 * @return
	 */
	public String getURL() {
		return URL;
	}

	/**
	 * Проверяет, был ли уже найден URL-адрес
	 */
	public static boolean check(LinkedList<URLDepthPair> resultLink, URLDepthPair pair) {
		boolean isAlready = true;
		for (URLDepthPair c : resultLink)
			if (c.getURL().equals(pair.getURL()))
				isAlready=false;
		return isAlready;
	}
}
