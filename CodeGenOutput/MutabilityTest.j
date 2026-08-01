.class public MutabilityTest
.super java/lang/Object

.method public <init>()V
    .limit stack 128
    .limit locals 128
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public calculateNewValue(I)I
    .limit stack 128
    .limit locals 128
    iload 1
    ldc 10
    iadd
    ireturn
.end method

