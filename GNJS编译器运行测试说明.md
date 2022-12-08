## GNJS编译器运行测试说明

1. 安装JDK

2. 打开终端

3. 测试词法分析
   * 运行``TestCases/ScannerTest.java``
4. 测试语法分析
   * 运行``TestCases/ParserTest.java``
5. 运行解释器
   * 例子一，判断连通图
   * 运行``TestCases/ExecTest.java`` 将filePath 换为 ``./input/connection.gnjs``
   * 例子二，二分查找
   * 运行``TestCases/ExecTest.java`` 将filePath 换为 ``./input/binarySearch.gnjs``
   * 例子三，入门手册
   * 运行``TestCases/ExecTest.java`` 将filePath 换为 ``./input/quickGuide.gnjs``
6. 运行REPL(交互式控制台模式)

   运行``TestCases/REPL.java``
   