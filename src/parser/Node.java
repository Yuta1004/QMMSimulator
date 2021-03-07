package parser;

class Node {
    public Node left, right;
    public double value;
    public String varName;
    public NodeKind kind;

    public Node(Node left, Node right, NodeKind kind){
        this.left = left;
        this.right = right;
        this.kind = kind;
    }

    public Node(double value) {
        this.value = value;
        this.kind = NodeKind.NUM;
    }

    public Node(String varName) {
        this.varName = varName;
        this.kind = NodeKind.VAR;
    }
}
