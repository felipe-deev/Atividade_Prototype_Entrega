package pedidos;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CatalogoDePedidos {
    private final Map<String, Pedido> modelos = new HashMap<>();

    public void registrar(String chave, Pedido modelo) {
        modelos.put(Objects.requireNonNull(chave), Objects.requireNonNull(modelo).clonar());
    }

    public Pedido criar(String chave) {
        Pedido modelo = modelos.get(chave);
        if (modelo == null) throw new IllegalArgumentException("Chave não registrada: " + chave);
        return modelo.clonar();
    }
}
