# Stream API

### 1. 创建流(Stream Creation)
用`Collection`中自带的`stream`方法创建流
```java
List<String> list = List.of("a", "b", "c");
Stream<String> stream = list.stream();
stream.forEach(System.out::println);
```
用数组创建流，调用`Arrays`中的`stream`方法
```java
String[] strings = {"a", "b", "c"};
Stream<String> stream = Arrays.stream(strings);
stream.forEach(System.out::println);
```
直接用`Stream.of`方法创建流
```java
Stream<String> stream = Stream.of("a", "b", "c");
stream.forEach(System.out::println);
```
合并两个流可以用`Stream.concat`
```java
Stream<String> stream1 = Stream.of("a", "b", "c");
Stream<String> stream2 = Stream.of("d", "e", "f");
Stream<String> concat = Stream.concat(stream1, stream2);
concat.forEach(System.out::println);
```
若想要动态构建流，可以使用`Stream.Builder`, 注意一旦调用了`build`方法就不能再向`Stream.Builder`添加新的元素
```java
Stream.Builder<String> stringBuilder = Stream.builder();
stringBuilder.add("a");
stringBuilder.add("b");
if (Math.random() > 0.5) {
    stringBuilder.add("c");
}
Stream<String> stream = stringBuilder.build();
stream.forEach(System.out::println);
```
处理文件(这里使用`try-with-recources`语句，保证最后会关闭文件)
```java
Path path = Paths.get("file.txt");
System.out.println(path);
try (Stream<String> lines = Files.lines(path)) {
    lines.forEach(System.out::println);
} catch (IOException e) {
    e.getStackTrace();
}
```
处理基本类型，以`IntStream`为例
```java
IntStream intStream = IntStream.of(1, 2, 3); //{1, 2, 3}
intStream.forEach(System.out::println);
IntStream range = IntStream.range(1, 4); //[1, 4), rangeClosed方法右边也为闭区间
range.forEach(System.out::println);
Stream<Integer> boxed = intStream.boxed(); //调用boxed方法将其装箱
```
创建无限流，可使用`generate`或`iterate`方法创建无限流，在创建无限流时为了防止死循环发生，常常利用`limit`限制流中元素的个数
```java
Stream<String> generateStream = Stream.generate(() -> "Hello").limit(5);
generateStream.forEach(System.out::println);

// Stream<Integer> iterateStream = Stream.iterate(0, i -> i + 2).limit(5);
Stream<Integer> iterateStream = Stream.iterate(0, i -> i < 10, i -> i + 2);
iterateStream.forEach(System.out::println);
```
默认时创建的流是顺序流，也可以调用`parallel`方法创建并行流，若为`Collection`类还可以直接调用`parallelStream`方法
```java
Stream<Integer> iterateStream = Stream.iterate(0, i -> i < 10, i -> i + 2);
iterateStream.parallel().forEach(System.out::println);

List.of("a", "b", "c").parallelStream().forEach(System.out::println);
```
### 2. 中间操作(Intermediate Operations)
在流中筛选出满足条件的元素，可以调用filter方法
```java
List<Person> people = List.of(new Person("Alan", 44, "US"),
                new Person("Bruce", 17, "US"),
                new Person("Crane", 19, "UK"),
                new Person("Dolly", 15, "CN"),
                new Person("Ella", 31, "FR")
                );

people.stream().filter(person -> person.getAge() > 18).forEach(System.out::println);
```
若要筛选掉重复的元素可以调用`distinct`方法，若元素为自定义类时，需要确保重写了`equals`和`hashCode`方法
```java
List<Person> people = List.of(new Person("Alan", 44, "US"),
                new Person("Bruce", 17, "US"),
                new Person("Crane", 19, "UK"),
                new Person("Dolly", 15, "CN"),
                new Person("Dolly", 15, "CN"), //重复人员
                new Person("Ella", 31, "FR"),
                new Person("Ella", 31, "FR") //重复人员
                );

people.stream().distinct().forEach(System.out::println);
```
`limit`和`skip`方法分别用来限制元素个数和跳过前面的元素
```java
List<Person> people = List.of(new Person("Alan", 44, "US"),
                new Person("Bruce", 17, "US"),
                new Person("Crane", 19, "UK"),
                new Person("Dolly", 15, "CN"),
                new Person("Ella", 31, "FR")
                );

people.stream().limit(2).forEach(System.out::println);
people.stream().skip(2).forEach(System.out::println);
```
`map`方法可以将当前类型的流映射为指定类型的流
```java
List<Person> people = List.of(new Person("Alan", 44, "US"),
                new Person("Bruce", 17, "US"),
                new Person("Crane", 19, "UK"),
                new Person("Dolly", 15, "CN"),
                new Person("Ella", 31, "FR")
                );

Stream<String> names = people.stream().map(Person::getName);
names.forEach(System.out::println);
```
`flatMap`可以将流中每一个元素都转换成另一个流，然后再将那些流合并成一个新的流， 通常用于处理嵌套结构的数据，例如流中的元素是集合，我们可以使用`flatMap`将所有集合中的元素平铺到一个新的流中。
```java
List<List<Person>> peopleGroup = List.of(List.of(new Person("Alan", 44, "US"),
                new Person("Bruce", 17, "US")),
                List.of(new Person("Crane", 19, "UK"),
                        new Person("Dolly", 15, "CN")),
                List.of(new Person("Ella", 31, "FR"))
                );

Stream<List<Person>> peopleGroupStream = peopleGroup.stream();
Stream<Person> personStream = peopleGroupStream.flatMap(Collection::stream);
personStream.forEach(System.out::println);
```
也可以用`mapToInt`, `mapToDouble`等方法将流转换为数值流
```java
List<Person> people = List.of(new Person("Alan", 44, "US"),
                new Person("Bruce", 17, "US"),
                new Person("Crane", 19, "UK"),
                new Person("Dolly", 15, "CN"),
                new Person("Ella", 31, "FR")
        );

people.stream().mapToInt(Person::getAge).forEach(System.out::println);
```
流的排序，调用`sorted`方法，和`sort`方法类似，如果实现了`Comparable`接口，则默认用`compareTo`方法进行排序，也可以直接实现`Comparator`
```java
Stream.of("banana", "apple", "watermelon", "cherry").
        sorted().forEach(System.out::println);

Stream.of("banana", "apple", "watermelon", "cherry").
        sorted(Comparator.comparingInt(String::length)).forEach(System.out::println);
```
以下代码为上述方法的综合使用
```java
List<List<Person>> peopleGroup = List.of(
                List.of(new Person("Alan", 44, "US"),
                        new Person("Bruce", 17, "US")),
                List.of(new Person("Crane", 19, "UK"),
                        new Person("Dolly", 15, "CN")),
                List.of(new Person("Ella", 31, "FR"))
        );

peopleGroup.stream()
        .flatMap(Collection::stream)
        .filter(person -> person.getAge() > 18)
        .sorted(Comparator.comparingInt(Person::getAge))
        .map(Person::getName)
        .forEach(System.out::println);
```
### 3. 终端操作(Terminal Operations)
在执行后终端操作后，流会被消耗，无法再进行其他操作，以下为常见的终端操作
#### 查找与匹配：
1. `allMatch`：检查流中的所有元素是否都满足指定的条件。如果所有元素都满足条件，则返回 `true`，否则返回 `false`。
```java
boolean allMatch = stream.allMatch(element -> element > 0);
```
2. `anyMatch`：检查流中是否至少有一个元素满足指定的条件。如果至少有一个元素满足条件，则返回 `true`，否则返回 `false`。
```java
boolean anyMatch = stream.anyMatch(element -> element == 0);
```
3. `noneMatch`：检查流中是否没有元素满足指定的条件。如果没有元素满足条件，则返回 `true`，否则返回 `false`。
```java
boolean noneMatch = stream.noneMatch(element -> element < 0);
```
4. `findFirst`：返回流中的第一个元素。
```java
Optional<String> first = stream.findFirst();
```
5. `findAny`：返回流中的任意一个元素。
```java
Optional<String> any = stream.findAny();
```
#### 聚合操作：
1. `count`：计算流中的元素数量。
```java
long count = stream.count();
```
2. `sum`、`average`：计算流中元素的总和或平均值。要求流中的元素类型为数值类型（如 `int`、`double`）。
```java
int sum = stream.mapToInt(Integer::intValue).sum(); // 计算整数流的总和
double average = stream.mapToDouble(Double::doubleValue).average().orElse(0.0); // 计算双精度浮点数流的平均值
```
3. `max`、`min`：查找流中的最大值或最小值。
```java
Optional<Integer> max = stream.max(Integer::compareTo); // 查找整数流的最大值
Optional<String> min = stream.min(String::compareTo); // 查找字符串流的最小值
```
4. `reduce`：对流中的元素进行归约操作，可以用来实现求和、求积等操作。
```java
int sum = stream.reduce(0, Integer::sum); // 计算整数流的总和
Optional<Integer> product = stream.reduce((a, b) -> a * b); // 计算整数流的乘积
```
5. `collect`：将流中的元素收集到集合中。
```java
List<Integer> list = stream.collect(Collectors.toList()); // 将流中的元素收集到列表中
Set<String> set = stream.collect(Collectors.toSet()); // 将流中的元素收集到集合中
```
#### 迭代操作：
在之前的代码中已经多次使用`for-each`方法，此处不再赘述

#### 收集器
在上面介绍的collect方法中，将`Collector`当作参数，常见的`Collector`有`Collectors.toList`, `Collectors.toSet`
也有分区和分组，如`Collectors.groupingBy`, `Collectors.partitioningBy`
也可以用`Collector.of`自定义`Collector`
具体相关用法可参考[Main.java](https://github.com/divine1022/StreamDemo/blob/master/src/Main.java)