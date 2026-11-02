
public class ABB {
    private Node raiz;   // raiz da árvore
    private int total;   // quantidade de pacientes na árvore

    ABB() {
        // Cria uma árvore binária vazia
        raiz = null;
        total = 0;
    }

    public boolean isEmpty() {
        return raiz == null;
    }

    public Node root() {
        return raiz;
    }

    public int tamanho() {
        return total;
    }

    /**
     * Insere um paciente usando o CPF como chave.
     * Retorna false (e não insere) se o CPF já existir na árvore.
     */
    public boolean insere(Paciente p) {
        String chave = p.getCpf();
        if (buscaNo(chave) != null) {
            return false; // CPF já cadastrado: evita duplicidade
        }

        Node z = new Node(p);
        Node y = null;
        Node x = raiz;
        while (x != null) {
            y = x;
            if (chave.compareTo(x.getDado().getCpf()) < 0) {
                x = x.getEsq();
            } else {
                x = x.getDir();
            }
        }
        z.setPai(y);
        if (y == null) {
            raiz = z;
        } else if (chave.compareTo(y.getDado().getCpf()) < 0) {
            y.setEsq(z);
        } else {
            y.setDir(z);
        }
        total++;
        return true;
    }

    /** Busca o nó que tem o CPF informado. Devolve null se não existir. */
    public Node buscaNo(String cpf) {
        Node y = raiz;
        while (y != null) {
            int cmp = cpf.compareTo(y.getDado().getCpf());
            if (cmp == 0) {
                return y;
            } else if (cmp > 0) {
                y = y.getDir();
            } else {
                y = y.getEsq();
            }
        }
        return null;
    }

    /** Busca o paciente pelo CPF. Devolve null se não existir. */
    public Paciente buscar(String cpf) {
        Node no = buscaNo(cpf);
        return (no == null) ? null : no.getDado();
    }
}
