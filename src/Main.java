import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        List<Person> people = List.of(new Person("Alan", 44, "US"),
                new Person("Bruce", 17, "US"),
                new Person("Crane", 19, "UK"),
                new Person("Dolly", 15, "CN"),
                new Person("Ella", 31, "FR")
        );

        //分组
        System.out.println("-----Group:");
        Map<String, List<Person>> peopleByCountry = people.stream().collect(Collectors.groupingBy(Person::getCountry));
        peopleByCountry.forEach((k, v) -> System.out.println(k + " -> " + v));

        //分类
        System.out.println("-----Partition:");
        Map<Boolean, List<Person>> agePartition = people.stream().collect(Collectors.partitioningBy(person -> person.getAge() > 18));
        agePartition.forEach((k, v) -> System.out.println(k + " -> " + v));

        //连接
        System.out.println("-----join:");
        String joinedName = people.stream().map(Person::getName).collect(Collectors.joining(", "));
        System.out.println(joinedName);

        //自定义Collector
        System.out.println("-----Collector:");
        ArrayList<Person> collect = people.parallelStream().collect(Collector.of(
                        ArrayList::new, // supplier: 用于创建一个新的可变结果容器的工厂函数。
                        (list, person) -> { //accumulator: 用于将元素累积到可变结果容器中的累积函数。
                            System.out.println("Accumulator: " + person);
                            list.add(person);
                        },
                        (left, right) -> { //combiner: 用于将两个可变结果容器合并成一个的组合函数。在并行流场景中会用到。
                            System.out.println("Combiner: " + left);
                            left.addAll(right);
                            return left;
                        },
                        Collector.Characteristics.IDENTITY_FINISH //characteristics: 用于描述收集器特征的枚举集合
        ));
        System.out.println(collect);
    }
}