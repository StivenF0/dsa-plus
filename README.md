# DSA-PLUS: Simulador de Streaming — v2

O **DSA-PLUS** é a segunda versão de uma aplicação de interface de linha de comando (CLI) desenvolvida em Java que simula a arquitetura Cliente-Servidor de uma plataforma de streaming (como Netflix ou Amazon Prime Video). O projeto foi concebido para a disciplina de Estruturas de Dados II, focando-se na implementação prática e análise de performance de estruturas de dados avançadas.

---

## Funcionalidades

- **Catálogo Realista:** Base de dados populada com 1000 filmes reais e populares, importados via API do TMDB (The Movie Database), com ano de lançamento e sinopse.
- **Busca Otimizada:** Pesquisa de filmes por ID (com hash table para busca indexada $O(1)$ e sem índice para busca sequencial $O(n)$) e por trechos ou prefixos do título.
- **Paginação por Categoria:** Navegação dinâmica e paginada por obras agrupadas por categoria (Ação, Drama, Comédia, etc.) em tempo constante.
- **Cache LRU:** Cada cliente possui cache local com capacidade de 50 filmes utilizando política **Least Recently Used** (LRU), implementada com HashMap + DoublyLinkedList para hits $O(1)$.
- **Multi-cliente:** Suporte a 3 usuários simultâneos (Aline, Paulo, Lucas), cada um com cache e árvore de preferências independentes.
- **Sistema de Recomendação:** Baseado em Splay Tree de preferências — o filme mais acessado (raiz da árvore) determina a categoria recomendada.
- **Rastreamento de Popularidade:** O servidor mantém uma Splay Tree de popularidade que se reorganiza a cada acesso, mantendo os filmes mais requisitados próximos à raiz.
- **Compressão Huffman:** Mensagens do sistema (LOGIN_OK, consultas, recomendações) são comprimidas com codificação de Huffman utilizando MinHeap próprio, exibindo taxa de compressão na análise final.
- **Bateria de Testes Integrada:** Execução automatizada de 20 consultas por cliente (inválidas, cache hit, com e sem índice) que contabilizam e reportam o número de comparações de cada estrutura de dados.
- **Análise Final:** Tela detalhada com top 10 LRU por cliente, top 5 preferências por cliente, top 10 popularidade do servidor, histórico de evicções LRU e estatísticas de compressão Huffman.
- **Logger Estruturado:** Sistema de logging com níveis (DEBUG/INFO/WARN/ERROR) e cores ANSI, suprimindo mensagens de debug durante a bateria de testes.
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
- **Rastreamento de Popularidade (`SplayTree`):** Árvore Splay que se reorganiza a cada acesso via operação `splay()`, mantendo os filmes mais requisitados próximos à raiz. A pré-carga do CSV insere os filmes em ordem reversa (menos populares primeiro), posicionando os TOPs do catálogo na raiz após todas as inserções.

### 2. Camada do Cliente (Frontend & Cache)

O cliente simula o comportamento de um dispositivo local focado em economia de banda e latência:

- **Cache LRU (`LRUCache`):** Combinação de HashMap (para lookup $O(1)$) com DoublyLinkedList para ordenar por recência. Ao atingir o limite de 50 filmes, remove o menos recentemente utilizado (cauda da lista). Hits movem o nó ao topo (head), mantendo os filmes mais acessados sempre disponíveis.
- **Árvore de Preferências (`SplayTree`):** Cada cliente mantém uma Splay Tree que armazena os filmes assistidos. A raiz da árvore contém o filme mais acessado, e sua categoria é utilizada como recomendação personalizada.

### 3. Compressão de Mensagens (Huffman)

- **`HuffmanCoding` e `MinHeap`:** Implementação de codificação de Huffman do zero. Constrói a árvore de compressão utilizando um MinHeap genérico para selecionar os dois menores nós a cada iteração. Comprime mensagens do sistema (LOGIN_OK, consultas, recomendações) e exibe taxa de compressão na análise final.

### 4. Sistema de Logging

- **`Logger`:** Sistema de logging com níveis DEBUG/INFO/WARN/ERROR, cores ANSI e formato padronizado `[LEVEL] [Tag] Mensagem`. Durante a bateria de testes, o nível é elevado para INFO, suprimindo mensagens de debug internas das estruturas de dados.

### 5. Camada de Apresentação (CLI)

- **`Main` e `CategoryConsoleViewer`:** Isolam as interações de menus, capturas de teclado e renderização de telas das regras de negócio do servidor ou do cliente. O menu principal oferece 3 clientes (Aline, Paulo, Lucas), bateria automatizada de 60 consultas e tela de análise final com estatísticas completas.

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
