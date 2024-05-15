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

### 3. 终端操作(Terminal Operations)