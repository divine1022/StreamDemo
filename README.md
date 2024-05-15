# Stream API

### 1. 创建流(Stream Creation)
用Collection中自带的stream方法创建流
```java
List<String> list = List.of("a", "b", "c");
Stream<String> stream = list.stream();
stream.forEach(System.out::println);
```
用数组创建流，调用Arrays中的stream方法
```java
String[] strings = {"a", "b", "c"};
Stream<String> stream = Arrays.stream(strings);
stream.forEach(System.out::println);
```
直接用Stream.of方法创建流
```java
Stream<String> stream = Stream.of("a", "b", "c");
stream.forEach(System.out::println);
```
合并两个流可以用Stream.concat
```java
Stream<String> stream1 = Stream.of("a", "b", "c");
Stream<String> stream2 = Stream.of("d", "e", "f");
Stream<String> concat = Stream.concat(stream1, stream2);
concat.forEach(System.out::println);
```
若想要动态构建流，可以使用Stream.Builder, 注意一旦调用了build方法就不能再向Stream.Builder添加新的元素
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
处理文件(这里使用try-with-recources语句，保证最后会关闭文件)
```java
Path path = Paths.get("file.txt");
System.out.println(path);
try (Stream<String> lines = Files.lines(path)) {
    lines.forEach(System.out::println);
} catch (IOException e) {
    e.getStackTrace();
}
```
处理基本类型，以IntStream为例
```java
IntStream intStream = IntStream.of(1, 2, 3); //{1, 2, 3}
intStream.forEach(System.out::println);
IntStream range = IntStream.range(1, 4); //[1, 4), rangeClosed方法右边也为闭区间
range.forEach(System.out::println);
Stream<Integer> boxed = intStream.boxed(); //调用boxed方法将其装箱
```
创建无限流，可使用generate或iterate方法创建无限流，在创建无限流时为了防止死循环发生，常常利用limit限制流中元素的个数
```java
Stream<String> generateStream = Stream.generate(() -> "Hello").limit(5);
generateStream.forEach(System.out::println);

// Stream<Integer> iterateStream = Stream.iterate(0, i -> i + 2).limit(5);
Stream<Integer> iterateStream = Stream.iterate(0, i -> i < 10, i -> i + 2);
iterateStream.forEach(System.out::println);
```
默认时创建的流是顺序流，也可以调用parallel方法创建并行流，若为Collection类还可以直接调用parallelStream方法
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
若要筛选掉重复的元素可以调用distinct方法，若元素为自定义类时，需要确保重写了equals和hashCode方法
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
limit和skip方法分别用来限制元素个数和跳过前面的元素
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
map方法可以将当前类型的流映射为指定类型的流
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
flatMap可以将流中每一个元素都转换成另一个流，然后再将那些流合并成一个新的流， 通常用于处理嵌套结构的数据，例如流中的元素是集合，我们可以使用 flatMap 将所有集合中的元素平铺到一个新的流中。
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
也可以用mapToInt, mapToDouble等方法将流转换为数值流
```java
List<Person> people = List.of(new Person("Alan", 44, "US"),
                new Person("Bruce", 17, "US"),
                new Person("Crane", 19, "UK"),
                new Person("Dolly", 15, "CN"),
                new Person("Ella", 31, "FR")
        );

people.stream().mapToInt(Person::getAge).forEach(System.out::println);
```
流的排序，调用sorted方法，和sort方法类似，如果实现了Comparable接口，则默认用compareTo方法进行排序，也可以直接实现Comparator
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