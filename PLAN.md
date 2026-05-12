# Arquitetura do Sistema

A nossa aplicação será dividida em quatro componentes lógicos principais, simulando a interação entre um dispositivo local (cliente) e um servidor remoto.

## 1. Entidade de Dados (Modelo)

- **`Filme`:** A classe que representa a unidade básica de informação. Conterá atributos como `ID` (inteiro, usado como chave), `Titulo` (texto) e `Categoria` (texto, para facilitar as buscas no console ).

## 2. Lado do Servidor (Backend)
O servidor detém o "catálogo mestre" e lida com a simulação do armazenamento físico.

- **Armazenamento Físico (`ListaLigada`):** Uma estrutura linear criada do zero, onde cada elemento aponta para o próximo, simulando o armazenamento em disco . A busca padrão nesta estrutura tem complexidade $O(n)$.

- **Indexador (`TabelaHash`):** Atua como um "mapa de endereços" para a lista ligada. A chave será o ID do filme e o valor será a referência (ponteiro) para o nó correspondente na lista ligada. Isso reduzirá o tempo de acesso para $O(1)$.

## 3. Lado do Cliente (Frontend/App)
O cliente atua como a interface de consumo e utiliza um cache local para evitar requisições desnecessárias à rede .

- **Cache Estrutural (`ArvoreAVL`):** Uma árvore binária de busca balanceada implementada do zero. Ela armazenará os filmes no cliente, garantindo que o tempo de verificação seja sempre logarítmico, ou seja, $O(\log n)$ .
- **Gestor de Cache (`Fila` para FIFO):** Para aplicar a política FIFO (First In, First Out), que remove o item mais antigo (inserido primeiro) no cache . Uma estrutura de Fila simples guardará os IDs dos filmes na ordem em que entram. Quando o cache atingir 50 itens, o primeiro ID da Fila será removido, e esse mesmo ID será usado para apagar o filme correspondente na `ArvoreAVL`.

## 4. Controlador / Simulador (Main)

* A classe principal que vai orquestrar o fluxo. Ela será responsável por instanciar o cliente e o servidor, popular os dados iniciais e conduzir as consultas, contando sempre o número de comparações realizadas .

---

# Plano de Implementação Passo-a-Passo

Para desenvolvermos isto de forma tranquila e testável, vamos seguir a seguinte ordem de codificação:

## Fase 1: As Fundações (Estruturas Base)

1. **Criar a classe `Filme`:** Definir atributos, construtor e métodos *get/set*.
2. **Desenvolver a `ListaLigada`:** Criar a classe `NoLista` e a classe `ListaLigada` com métodos básicos de inserção (para popular o servidor) e busca sequencial (com um contador de comparações).

## Fase 2: Acelerando o Servidor (Indexação)

1. **Desenvolver a `TabelaHash`:** Criar a classe com uma função de *hash* simples (como o módulo do ID). Implementar o método para inserir a referência do nó da lista e o método de busca na tabela (também com contador de comparações).
2. **Integrar o Servidor:** Criar a classe `Servidor` que une a `ListaLigada` e a `TabelaHash`, além de um método para carregar os 1000 registros fictícios.

## Fase 3: Inteligência no Cliente (Cache)

1. **Desenvolver a `ArvoreAVL`:** Criar a classe `NoAVL` e a lógica complexa da árvore (inserção, rotações para balanceamento, busca e remoção por ID). A busca deve retornar também quantas comparações foram feitas.
2. **Desenvolver a `Fila` (FIFO):** Criar uma estrutura de fila simples para guardar IDs.
3. **Integrar o Cliente:** Criar a classe `Cliente` que contém a `ArvoreAVL` (limite de 50 nós) e a `Fila`. Implementar a lógica: tentar buscar no cache (Hit), e se falhar (Miss), pedir ao Servidor e inserir no cache, disparando o esvaziamento (Eviction) se necessário .

## Fase 4: A Simulação (Testes e Requisitos Finais)

1. **Criar a classe `Main`:** Inicializar tudo. Carregar 50 registros no cache.
2. **Executar as Consultas Requisitadas:** Rodar o bloco de 20 consultas (2 inválidas, 6 no cache, 6 sem índice e 6 com índice) e exibir no console o número de comparações de cada uma .

Como este plano de arquitetura e implementação soa para ti? Gostarias de fazer algum ajuste antes de começarmos a codificar a **Fase 1**, ou podemos avançar diretamente para a criação da classe `Filme` e da `ListaLigada`?
