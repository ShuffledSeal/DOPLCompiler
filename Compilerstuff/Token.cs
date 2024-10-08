public class Token
{
    public string Type { get; private set; }
    public string Value { get; private set; }

    public Token(string type, string value)
    {
        Type = type;
        Value = value;
    }

    public override string ToString()
    {
        return $"Token(Type: {Type}, Value: {Value})";
    }
}
