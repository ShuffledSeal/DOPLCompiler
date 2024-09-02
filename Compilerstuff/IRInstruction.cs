public class IRInstruction
{
    public string Operation { get; private set; }
    public string Operand1 { get; private set; }
    public string Operand2 { get; private set; }
    public string Result { get; private set; }

    public IRInstruction(string operation, string operand1, string operand2, string result)
    {
        Operation = operation;
        Operand1 = operand1;
        Operand2 = operand2;
        Result = result;
    }

    public override string ToString()
    {
        return $"IRInstruction(Operation: {Operation}, Operand1: {Operand1}, Operand2: {Operand2}, Result: {Result})";
    }
}
