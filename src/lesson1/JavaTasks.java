package lesson1;

import kotlin.NotImplementedError;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

@SuppressWarnings("unused")
public class JavaTasks {
    /**
     * Сортировка времён
     *
     * Простая
     * (Модифицированная задача с сайта acmp.ru)
     *
     * Во входном файле с именем inputName содержатся моменты времени в формате ЧЧ:ММ:СС AM/PM,
     * каждый на отдельной строке. См. статью википедии "12-часовой формат времени".
     *
     * Пример:
     *
     * 01:15:19 PM
     * 07:26:57 AM
     * 10:00:03 AM
     * 07:56:14 PM
     * 01:15:19 PM
     * 12:40:31 AM
     *
     * Отсортировать моменты времени по возрастанию и вывести их в выходной файл с именем outputName,
     * сохраняя формат ЧЧ:ММ:СС AM/PM. Одинаковые моменты времени выводить друг за другом. Пример:
     *
     * 12:40:31 AM
     * 07:26:57 AM
     * 10:00:03 AM
     * 01:15:19 PM
     * 01:15:19 PM
     * 07:56:14 PM
     *
     * В случае обнаружения неверного формата файла бросить любое исключение.
     *
     * O(n^2) - insertion sort
     */
    static public void sortTimes(String inputName, String outputName) throws IOException
    {
        List<String> timeStrings = getStrings(inputName);
        File file;

        Time[] times = new Time[timeStrings.size()];

        for (int i = 0; i < timeStrings.size(); i++)
        {
            times[i] = new Time(timeStrings.get(i));
        }

        Sorts.insertionSort(times);

        file = new File(outputName);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file)))
        {
            for (int i = 0; i < times.length-1; i++)
            {
                bw.append(times[i].data);
                bw.append(System.lineSeparator());
            }
            bw.append(times[times.length-1].data);
            bw.flush();
        }
    }

    @NotNull
    private static List<String> getStrings(String inputName) throws IOException
    {
        File file = new File(inputName);
        List<String> timeStrings = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file))))
        {
            String s;

            while ((s = br.readLine()) != null)
            {
                timeStrings.add(s);
            }
        }


        return timeStrings;
    }

    static class Time implements Comparable
    {
        byte h;
        byte m;
        byte s;
        boolean isAM;
        String data;

        Time(String timeString)
        {
            String isAMString;
            try
            {
                isAMString = timeString.substring(timeString.length()-2);
            }
            catch (IndexOutOfBoundsException e)
            {
                throw new IllegalArgumentException();
            }

            if (isAMString.equals("AM"))
                isAM = true;
            else if (isAMString.equals("PM"))
                isAM = false;
            else
                throw new IllegalArgumentException();

            String[] strings = timeString.substring(0, timeString.length()-3).split(":");
            if (strings.length != 3) throw new IllegalArgumentException();

            try
            {
               h = getByte(strings[0]);
               m = getByte(strings[1]);
               s = getByte(strings[2]);

            }
            catch (NumberFormatException e)
            {
                throw new IllegalArgumentException();
            }

            if (h < 1 || h > 12)
                throw new IllegalArgumentException();
            if (m < 0 || m > 59)
                throw new IllegalArgumentException();
            if (s < 0 || s > 59)
                throw new IllegalArgumentException();

            data = timeString;
        }

        private byte getByte(String s) throws NumberFormatException
        {
            if (s.length() != 2)
                throw new NumberFormatException();
            if (s.charAt(0) == '0' )
                s = s.substring(1);

            return Byte.parseByte(s);
        }

        @Override
        public int compareTo(@NotNull Object o)
        {
            Time t = (Time)o;

            if (isAM && !t.isAM)
                return -1;
            if (!isAM && t.isAM)
                return 1;

            if (h > t.h)
                if (h == 12)
                    return -1;
                else
                    return 1;
            if (h < t.h)
                if (t.h == 12)
                    return 1;
                else
                    return -1;

            if (m > t.m)
                return 1;
            if (m < t.m)
                return -1;

            return Byte.compare(s, t.s);
        }
    }

    /**
     * Сортировка адресов
     *
     * Средняя
     *
     * Во входном файле с именем inputName содержатся фамилии и имена жителей города с указанием улицы и номера дома,
     * где они прописаны. Пример:
     *
     * Петров Иван - Железнодорожная 3
     * Сидоров Петр - Садовая 5
     * Иванов Алексей - Железнодорожная 7
     * Сидорова Мария - Садовая 5
     * Иванов Михаил - Железнодорожная 7
     *
     * Людей в городе может быть до миллиона.
     *
     * Вывести записи в выходной файл outputName,
     * упорядоченными по названию улицы (по алфавиту) и номеру дома (по возрастанию).
     * Людей, живущих в одном доме, выводить через запятую по алфавиту (вначале по фамилии, потом по имени). Пример:
     *
     * Железнодорожная 3 - Петров Иван
     * Железнодорожная 7 - Иванов Алексей, Иванов Михаил
     * Садовая 5 - Сидоров Петр, Сидорова Мария
     *
     * В случае обнаружения неверного формата файла бросить любое исключение.
     *
     * O(n*log(n)) - tree sort
     */
    static public void sortAddresses(String inputName, String outputName) throws IOException
    {
        Map<Address, List<Name>>  map = new TreeMap<>();

        File file = new File(inputName);

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file))))
        {
            String s;

            while ((s = br.readLine()) != null)
            {
                String[] strings = s.split(" - ");
                if (strings.length != 2) throw new IllegalArgumentException();
                Address a = new Address(strings[1]);

                if (!map.containsKey(a))
                {
                    List<Name> as = new ArrayList<>();
                    as.add(new Name(strings[0]));
                    map.put(a, as);
                }
                else
                {
                    map.get(a).add(new Name(strings[0]));
                    Collections.sort(map.get(a));
                }
            }
        }


        file = new File(outputName);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file)))
        {
            for (Map.Entry e : map.entrySet())
            {
                bw.append(e.getKey().toString());
                bw.append(" - ");
                bw.append(e.getValue().toString().substring(1, e.getValue().toString().length()-1));
                bw.append(System.lineSeparator());
            }
            bw.flush();
        }
    }

    static class Name implements Comparable
    {
        String name;
        Name(String s)
        {
            if (s.split(" ").length != 2) throw new IllegalArgumentException();
            name = s;
        }

        @Override
        public int compareTo(@NotNull Object o)
        {
            Name n = (Name)o;
            if (name.compareTo(n.name) != 0)
                return name.compareTo(n.name)/Math.abs(name.compareTo(n.name));
            return 0;
        }

        @Override
        public String toString()
        {
            return name;
        }
    }

    static class Address implements Comparable
    {
        String street;
        int building;

        Address(String addr)
        {
            String[] strings = addr.split(" ");
            if (strings.length != 2) throw new IllegalArgumentException();

            street = strings[0];
            building = Integer.parseInt(strings[1]);
            if (building < 1) throw new IllegalArgumentException();
        }

        @Override
        public int compareTo(@NotNull Object o)
        {
            Address a = (Address) o;

            if (street.compareTo(a.street) != 0)
                return street.compareTo(a.street)/Math.abs(street.compareTo(a.street));
            else
                if (building != a.building)
                    return Integer.compare(building, a.building);
                else
                    return 0;
        }

        @Override
        public String toString()
        {
            return street + " " + building;
        }

        @Override
        public boolean equals(Object o)
        {
            return street.equals(((Address)o).street) && building == ((Address)o).building;
        }
    }

    /**
     * Сортировка температур
     *
     * Средняя
     * (Модифицированная задача с сайта acmp.ru)
     *
     * Во входном файле заданы температуры различных участков абстрактной планеты с точностью до десятых градуса.
     * Температуры могут изменяться в диапазоне от -273.0 до +500.0.
     * Например:
     *
     * 24.7
     * -12.6
     * 121.3
     * -98.4
     * 99.5
     * -12.6
     * 11.0
     *
     * Количество строк в файле может достигать ста миллионов.
     * Вывести строки в выходной файл, отсортировав их по возрастанию температуры.
     * Повторяющиеся строки сохранить. Например:
     *
     * -98.4
     * -12.6
     * -12.6
     * 11.0
     * 24.7
     * 99.5
     * 121.3
     */
    static public void sortTemperatures(String inputName, String outputName) {
        throw new NotImplementedError();
    }

    /**
     * Сортировка последовательности
     *
     * Средняя
     * (Задача взята с сайта acmp.ru)
     *
     * В файле задана последовательность из n целых положительных чисел, каждое в своей строке, например:
     *
     * 1
     * 2
     * 3
     * 2
     * 3
     * 1
     * 2
     *
     * Необходимо найти число, которое встречается в этой последовательности наибольшее количество раз,
     * а если таких чисел несколько, то найти минимальное из них,
     * и после этого переместить все такие числа в конец заданной последовательности.
     * Порядок расположения остальных чисел должен остаться без изменения.
     *
     * 1
     * 3
     * 3
     * 1
     * 2
     * 2
     * 2
     */
    static public void sortSequence(String inputName, String outputName) {
        throw new NotImplementedError();
    }

    /**
     * Соединить два отсортированных массива в один
     *
     * Простая
     *
     * Задан отсортированный массив first и второй массив second,
     * первые first.size ячеек которого содержат null, а остальные ячейки также отсортированы.
     * Соединить оба массива в массиве second так, чтобы он оказался отсортирован. Пример:
     *
     * first = [4 9 15 20 28]
     * second = [null null null null null 1 3 9 13 18 23]
     *
     * Результат: second = [1 3 4 9 9 13 15 20 23 28]
     */
    static <T extends Comparable<T>> void mergeArrays(T[] first, T[] second) {
        throw new NotImplementedError();
    }
}
