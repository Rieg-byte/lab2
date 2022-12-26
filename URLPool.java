import java.util.LinkedList;

/**
 * Класс хранит список всех URL-адресов для поиска, а также относительный
 * уровень каждого из этих URL-адресов(глубина поиска)
 */
public class URLPool {
	LinkedList<URLDepthPair> findLink; // то что надо найти ссылки
	LinkedList<URLDepthPair> viewedLink; // ссылки, которые уже были просмотрены
	private int maxDepth; // максимальная глубина
	private int cWait; // количество ожидающих потоков

	// конструктор
	public URLPool(int maxDepth) {
		this.maxDepth = maxDepth;
		findLink = new LinkedList<URLDepthPair>();
		viewedLink = new LinkedList<URLDepthPair>();
		cWait = 0;
	}

	// Возвращает пару
	public synchronized URLDepthPair getPair() {
		// пока размер = 0
		while (findLink.size() == 0) {
			// увеличивается перед вызовом wait()
			cWait++;
			try {
				wait();
			}
			catch (InterruptedException e) {
				System.out.println("InterruptedException is ignored");
			}
			// уменьшается после выхода из режима ожидания
			cWait--;
		}
		// удалаяем первую ссылку и возвращаем
		URLDepthPair nextPair = findLink.removeFirst();
		return nextPair;
	}

	// Метод добавления пары
	public synchronized void addPair(URLDepthPair pair)
	{
		// проверяет содержится ли пара в просмотренных ссылках
		if(URLDepthPair.check(viewedLink,pair))
		{
			// Если нет, то добавляем в просмотренные и проверяем ее глубину
			viewedLink.add(pair);
			//если глубина меньше максимальной, то добавляем в findLink и разбужаем поток
			if (pair.getDepth() < maxDepth)
			{
				findLink.add(pair);
				notify();
			}
		}
	}
	// Получаем количество ожидающих потоков
	public synchronized int getWait()
	{
		return cWait;
	}

	// Возвращаем просмотренные сайты
	public LinkedList<URLDepthPair> getResult()
	{
		return viewedLink;
	}
}
