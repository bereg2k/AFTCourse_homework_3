import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Задание "Чтение из файла".
 * Есть входной файл с набором слов, написанных через пробел. Необходимо:
 * - Прочитать слова из файла.
 * - Отсортировать в алфавитном порядке.
 * - Посчитать сколько раз каждое слово встречается в файле.
 * - Вывести статистику на консоль
 * - Найти слово с максимальным количеством повторений.
 * - Вывести на консоль это слово и сколько раз оно встречается в файле
 * <p>
 * Особенности:
 * - На выбор предлагаются два файла: первый - где самое длинное слово только одно, второй - где их несколько.
 * - Программа выводит на консоль содержимое выбранного файла.
 */
public class Base {
    public static void main(String[] args) throws FileNotFoundException {

        Scanner scanner = new Scanner(System.in); //консольный ввод
        File file = null; //переменная для будущего текстового файла
        int fileChoice; //переменная для выбора файла

        System.out.println("Это программа поиска самого частого слова файле!");
        System.out.println("Пожалуйста, выберите файл для обработки.");
        System.out.println("- Введите \"1\" для выбора файла, где самое частое слово только ОДНО.");
        System.out.println("- Введите \"2\" для выбора файла, где НЕСКОЛЬКО самых частых слов.");
        fileChoice = scanner.nextInt();

        if (fileChoice == 1) {//выбираем текстовый файл с одним длинным словом
            file = new File("text_with_1_max_word.txt"); //текстовый файл (есть в проектной директории)
        } else if (fileChoice == 2) {//выбираем текстовый файл с несколькими самыми частыми словами
            file = new File("text_with_2+_max_words.txt");
        } else {//обработка некорректного ввода
            System.out.println("Invalid input! Program aborted!");
            System.exit(1);
        }

        printTextFile(file); //распечатываем выбранный файл на консоль

        //это компаратор для игнорирования регистра при формировании списка уникальных слов
        Comparator<String> comparator = String::compareToIgnoreCase;

        List<String> wordsUnique = new ArrayList<>(); //коллекция для списка уникальных слов в файле
        Map<String, Integer> wordsQuantity = new HashMap<>(); //коллекция "слово-количество" в файле

        findUniqueWordsAndQuantity(file, wordsUnique, wordsQuantity);

        wordsUnique.sort(comparator); //сортируем уникальные слова
        removeDuplicatesFromTheList(wordsUnique); //убираем дубликаты для формирования итогового списка уникальных слов

        System.out.println("\nВот список всех слов из файла (в алфавитном порядке): ");
        System.out.println(wordsUnique);

        System.out.println("\nВот список всех слов и их соответствующего количества в файле: ");
        System.out.println(wordsQuantity);

        System.out.println();

        String maxWord = null; // самое частое слово в файле
        int maxQuantity = 1; // общее количество раз, которое самое частое слово встречается в файле
        int maxCount = 0; // общее количество самых частых слов (если больше одного)
        List<String> maxWordList = new ArrayList<>(); //коллекция на случай, если самых частых слов несколько

        for (Object o : wordsQuantity.entrySet()) { //проход по коллекция для поиска самого частого слова в файле
            Map.Entry pair = (Map.Entry) o;
            if (maxQuantity < (int) pair.getValue()) {
                maxQuantity = (int) pair.getValue();
                maxWord = (String) pair.getKey();
            }
        }

        for (Object o : wordsQuantity.entrySet()) { //проход по коллекция для подсчета количества самых частых слов
            Map.Entry pair = (Map.Entry) o;
            if (maxQuantity == (int) pair.getValue()) {
                maxWordList.add((String) pair.getKey());
                maxCount++;
            }
        }

        //печать результата по самому частому слову в файле
        if (maxCount >= 2) { //если самых частых слов больше одного
            System.out.println("У нас больше одного самого частого слова в файле!");
            System.out.println("Вот эти слова: " + maxWordList);
            System.out.println("Каждое слово найдено " + maxQuantity + " раз(а).");

        } else { //если только одно самое частое слово
            System.out.println("Самое частое слово в файле - \"" + maxWord + "\".");
            System.out.println("Оно встречается здесь " + maxQuantity + " раз(а).");
        }
    }

    /**
     * Печать стандартного текстового файла на консоль.
     *
     * @param textFilePath путь к текстовому файлу
     * @throws FileNotFoundException исключение на случай, если файл не найден
     */
    private static void printTextFile(File textFilePath) throws FileNotFoundException {

        Scanner scannerToPrint = new Scanner(textFilePath); //буферная переменная для содержимого файла
        System.out.println("Вот содержимое вашего файла \"" + textFilePath.getAbsolutePath() + "\":\n");
        System.out.print("\"");
        while (scannerToPrint.hasNextLine()) { // печатаем содержимое на консоль
            String line = scannerToPrint.nextLine();
            System.out.print(line);
            if (!scannerToPrint.hasNextLine()) //аккуратный способ закончить печать файла кавычками
                System.out.println("\"");
            else
                System.out.println();
        }
        scannerToPrint.close();
    }

    /**
     * Данный метод собирает все уникальные слова из текстового файла,
     * сортирует их по алфавиту и считает количество каждого из них.
     *
     * @param textFilePath путь к текстовому файлу
     * @param words        коллекция отсортированных уникальных слов (List)
     * @param quantity     коллекция типа "слово-количество" (Map)
     */
    private static void findUniqueWordsAndQuantity(File textFilePath, List<String> words, Map<String, Integer> quantity)
            throws FileNotFoundException {
        Scanner scanner = new Scanner(textFilePath); //буферная переменная для содержимого файла
        while (scanner.hasNext()) {
            String delimiter = "(\\s+|\\.|,|[?]|[!]|\"|[(]|[)])"; //различные разделители
            String word = scanner.useDelimiter(delimiter).next();

            if (word.equals("")) //доработка для разделителя выше: пропускаем пустые слова, которые он ловит
                continue;

            Integer count = quantity.get(word);
            if (count == null) {
                count = 0;
            }
            quantity.put(word, ++count); //считаем количество уникальных слов в файле
            words.add(word); // собираем уникальные слова в отсортированном виде
        }
        scanner.close();
    }

    /**
     * Данный метод убирает строковые дубликаты из списков List.
     *
     * @param words массив слов
     */
    private static void removeDuplicatesFromTheList(List<String> words) {
        for (int i = 0; i < words.size(); i++) {
            String nextWord = words.get(i);
            for (int j = 0; j < words.size(); j++) {
                if (i != j && nextWord.equals(words.get(j))) {
                    words.remove(j);
                    j = 0;
                }
            }
        }
    }
}
