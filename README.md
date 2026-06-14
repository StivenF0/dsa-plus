# DSA-PLUS: Simulador de Streaming — v2

O **DSA-PLUS** é a segunda versão de uma aplicação de interface de linha de comando (CLI) desenvolvida em Java que simula a arquitetura Cliente-Servidor de uma plataforma de streaming (como Netflix ou Amazon Prime Video). O projeto foi concebido para a disciplina de Estruturas de Dados II, focando-se na implementação prática e análise de performance de estruturas de dados avançadas.

---

## Funcionalidades

- **Catálogo Realista:** Base de dados populada com 1000 filmes reais e populares, importados via API do TMDB (The Movie Database).
- **Busca Otimizada:** Pesquisa de filmes por ID (com e sem uso de índices) e por trechos ou prefixos do título.
- **Paginação por Categoria:** Navegação dinâmica e paginada por obras agrupadas por categoria (Ação, Drama, Comédia, etc.) em tempo constante.
- **Sistema de Cache Local:** Simulação de um dispositivo cliente com memória limitada a 50 itens, utilizando cache para respostas quase instantâneas.
- **Bateria de Testes Integrada:** Execução automatizada de 20 consultas simuladas que contabilizam e reportam o número exato de comparações feitas por cada estrutura de dados.
- **Resiliência e Proteção contra Falhas:** Tratamento robusto de exceções (como `InputMismatchException` no teclado) e validação de limites de páginas ou IDs inválidos.

---

## Decisões Arquiteturais e Design do Sistema

O sistema foi desenhado seguindo os princípios de _Clean Code_ e separação de responsabilidades (_Separation of Concerns_), dividindo-se em componentes claros:

### 1. Camada do Servidor (Backend & Catálogo Mestre)

O servidor atua como o repositório central e persistente de dados, otimizado para buscas rápidas:

- **Armazenamento Físico (`LinkedList`):** Implementada totalmente do zero, simula o armazenamento sequencial em disco. A busca direta aqui possui complexidade $O(n)$.
- **Indexação Primária por ID (`HashTable`):** Utilizada para buscas instantâneas de filmes por ID. Implementa o **Método da Multiplicação de Knuth** ($A \approx 0.618$ baseado na proporção áurea) para a função de dispersão, mitigando colisões através de encadeamento separado (_Separate Chaining_). Reduz o tempo de busca no servidor para $O(1)$.
- **Indexação por Categoria (`categoryIndex`):** Utiliza a estrutura `HashMap` nativa do Java para mapear categorias a listas de filmes, permitindo fatiar e paginar os resultados de forma imediata em $O(1)$.
- **Indexação por Prefixo de Título (`TitlePrefixIndex`):** Tabela Hash customizada baseada em strings (utilizando _Polynomial Rolling Hash_) para acelerar a pesquisa de títulos pelos 3 primeiros caracteres.

### 2. Camada do Cliente (Frontend & Cache)

O cliente simula o comportamento de um dispositivo local focado em economia de banda e latência:

- **Cache Estrutural (`AVLTree`):** Implementação baseada em uma árvore binária de busca rigidamente balanceada através de rotações simples e duplas. Garante consultas locais em tempo $O(\log n)$.
- **Política de Esvaziamento (Cache Eviction - FIFO):** O cache possui um limite fixo de 50 nós. Para evitar varreduras caras na árvore, utiliza uma fila auxiliar nativa (`CacheQueue`) que rastreia a ordem de entrada dos IDs. Quando o limite é atingido, o ID mais antigo é desenfileirado e removido da árvore AVL, mantendo a operação em complexidade $O(\log n)$.

### 3. Camada de Apresentação (CLI)

- **`Main` e `CategoryConsoleViewer`:** Isolam as interações de menus, capturas de teclado e renderização de telas das regras de negócio do servidor ou do cliente.

---

## Como Executar o Projeto

### Pré-requisitos

- Java JDK 21 ou superior instalado.
- O projeto vem configurado com o **Gradle Wrapper**, eliminando a necessidade de instalação prévia do Gradle.

### Executando a Aplicação

Abra o terminal na pasta raiz do projeto (`dsa-plus`) e execute o comando correspondente ao seu sistema operacional:

**No Windows:**

```bash
gradlew run

```

**No Linux / macOS:**

```bash
./gradlew run

```

O menu interativo será renderizado e ficará aguardando a interação do utilizador no terminal.

---

## Estrutura de Diretórios Principal

```text
dsa-plus/
├── app/src/
│   └── main/
│       ├── java/com/dsaplus/
│       │   ├── client/         # Componentes do Cliente (LRUCache, SplayTree preferências)
│       │   ├── common/ds/      # Estruturas compartilhadas (SplayTree genérica)
│       │   ├── model/          # Modelagem de Dados e Entidades (Movie)
│       │   ├── server/         # Componentes do Servidor (HashTables, LinkedList, SplayTree)
│       │   ├── util/           # Utilitários (CsvParser, HuffmanCoding, Logger, MinHeap)
│       │   └── Main.java       # Ponto de Entrada da Interface CLI
│       └── resources/csv/      # Dataset real do TMDB (movies_dataset.csv)
│
└── build.gradle.kts            # Arquivo de script de compilação do Gradle
```
