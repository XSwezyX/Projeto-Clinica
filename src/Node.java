/**
 * Nó da ABB. Guarda um Paciente (chave = CPF do paciente).
 * Adaptado do Node da professora (ProjABB): elemento -> dado,
 * mantendo os ponteiros left/right/parent.
 */
public class Node {
    private Paciente dado;
    private Node esq;     // filho da esquerda (CPF menor)
    private Node dir;     // filho da direita (CPF maior)
    private Node pai;     // nó pai (usado nas remoções)

    Node(Paciente dado) {
        this.dado = dado;
        esq = dir = pai = null;
    }

    public Paciente getDado() { return dado; }

    public Node getEsq() { return esq; }
    public void setEsq(Node esq) { this.esq = esq; }

    public Node getDir() { return dir; }
    public void setDir(Node dir) { this.dir = dir; }

    public Node getPai() { return pai; }
    public void setPai(Node pai) { this.pai = pai; }
}
