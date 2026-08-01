.class public Main
.super MutabilityTest

.method public <init>()V
    .limit stack 128
    .limit locals 128
    aload_0
    invokespecial MutabilityTest/<init>()V
    return
.end method

.method public main()V
    .limit stack 128
    .limit locals 128
    ldc 5
    istore 1
    aload_0
    iload 1
    invokevirtual MutabilityTest/calculateNewValue(I)I
    istore 1
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload 1
    invokevirtual java/io/PrintStream/println(I)V
    return
.end method

.method public static main([Ljava/lang/String;)V
    .limit stack 128
    .limit locals 128
    new Main
    dup
    invokespecial Main/<init>()V
    invokevirtual Main/main()V
    return
.end method
