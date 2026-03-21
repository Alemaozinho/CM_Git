Assignment 1 - Kotlin Fundamentals & Virtual Library

Course: Engenharia Informática e de Computadores 

Student: Lucas Alemão

Date: 15/03/2026

Repository URL : https://github.com/Alemaozinho/CM_Git/tree/main

1. Introdução
Este relatório descreve a implementação da primeira fase do tutorial de Computação Móvel, focada na aprendizagem da linguagem Kotlin através do IntelliJ IDEA. O foco principal foi o desenvolvimento de lógica de programação, controlo de fluxo e a aplicação de conceitos avançados de Programação Orientada a Objetos (POO).

2. Visão Geral do Sistema
O sistema resolve três desafios distintos propostos no tutorial:
  Exercícios de Lógica (2.1 a 2.3): Manipulação de arrays de quadrados perfeitos, calculadora de consola com tratamento de exceções e modelagem de sequências de saltos de uma bola.
  System Info App (5.3): Extração de metadados do hardware usando o objeto android.os.Build.
  Conversor de Moedas (MIP-1): Aplicação Android nativa para conversão de EUR para USD com taxa fixa de 1.16.

3. Arquitetura e Design
A arquitetura do projeto foi estruturada para separar claramente a lógica de consola da interface móvel:
  Estrutura de Pacotes: Seguindo as diretrizes do tutorial, foram criados pacotes específicos (cm.exer_1 a cm.exer_3 e cm.virtual_library) para organizar os exercícios de lógica e o sistema de POO.
  Design de Interface (Android): Utilização de Layout Containers (camada de apresentação) para organizar Widgets (elementos de UI). O uso de ConstraintLayout permitiu um design flexível e responsivo, essencial para a adaptação a diferentes resoluções.
  Padrões de POO: Implementação de uma base sólida com a classe abstrata Livro, permitindo a extensibilidade do sistema para novos formatos através de herança.

4. Implementação (Detalhes Técnicos)
Exercícios Kotlin (IntelliJ + Maven)
  Exer 1: Criação de IntArray de 50 posições para quadrados perfeitos utilizando map() e construtores de Array.
  Exer 2 (Calculadora): Uso de when para operações aritméticas e bitwise (shl, shr), com tratamento de divisão por zero.
  Exer 3 (Sequências): Uso de generateSequence para calcular alturas de ressalto (60% da anterior), filtrando valores inferiores a 1m.
Biblioteca Virtual (POO)
  Abstração: Classe Book com init block para log de criação.
  Encapsulamento: Getter customizado para publicationYear (Classic/Modern/Contemporary) e Setter para availableCopies com validação de stock.
  Polimorfismo: Método abstrato getStorageInfo() implementado de forma distinta em DigitalBook (fileSize/format) e PhysicalBook (weight/hardcover).
  Companion Object: Implementado em Library para contar o total de livros criados globalmente.

5. Testes e Validação
Cenário 1 (POO): Tentativa de borrowBook com 0 cópias; resultado esperado: mensagem de "out of stock".
Cenário 2 (Android): Verificação de logs no Logcat através do comando println no onCreate, validando o ciclo de vida da Activity.
Cenário 3 (Cálculo): Conversão de 100€ resultando em $116.00 (Taxa: 1.16).

6. Instruções de UtilizaçãoConfiguração:
O projeto de lógica deve ser aberto no IntelliJ IDEA com suporte Maven.
Execução: Para a aplicação móvel, utilizar o emulador Pixel 9 Pro (API 34) no Android Studio.
Interação: Introduzir um valor decimal no campo de texto e clicar no botão de conversão para ver o resultado formatado.

7. Estratégia de Prompting
Para o desenvolvimento da interface e lógica de conversão, utilizei a técnica de Direct Prompting focada em Engenharia de Software Autónoma. 
A estrutura dos prompts seguiu o modelo recomendado pelo enunciado:Contexto: "Atua como um engenheiro de software Android especialista em Kotlin e Material Design 3".Objetivo: "Criar um conversor de moedas funcional (EUR para USD) com uma taxa fixa de 1.16".
Restrições: "Utilizar ConstraintLayout, garantir que o título não seja obstruído pelo notch do Pixel 9 Pro (margem de 60dp) e aplicar inputType="numberDecimal" no campo de texto".

8. Suporte da IA na Lógica de Conversão
A utilização da IA (Gemini) foi fundamental para converter os requisitos de negócio em código funcional e seguro, especialmente nas seguintes áreas:
  Conversão de Tipos: Suporte na transformação de Editable (do EditText) para Double de forma segura através de toDoubleOrNull().
  Resolução de Erros de Threading: Auxílio crítico na correção da IllegalStateException na EDT (Event Dispatch Thread), garantindo que a lógica de UI permanecia na thread principal enquanto o processamento era validado.
  Formatação de Output: Geração de código para formatar o resultado final com duas casas decimais utilizando String.format("%.2f", resultado).

9. Verificação de Artefactos Gerados por IA
Todos os componentes gerados foram validados através de: 
  Revisão Manual: Garantir que os IDs do XML coincidiam com as referências no Kotlin para evitar erros de compilação. 
  Debug: Uso de breakpoints e Step Over para verificar se a taxa de 1.16 era aplicada corretamente sem bloquear a thread principal.

10. Contribuição Humana vs AI
A estrutura inicial foi apoiada por ferramentas de IA e de pesquisas online. A adaptação final, tradução integral para português e a resolução de conflitos de lógica foram realizadas manualmente.

11. Uso Ético e Responsável
A IA foi utilizada de forma supervisionada para acelerar a aprendizagem. Mantive o controlo sobre a integridade do código, garantindo que o erro de EDT (IllegalStateException) fosse resolvido e compreendido

12. Histórico de Commits
O histórico de commits no GitHub foi mantido de forma contínua para refletir a progressão real do trabalho:
  Inicialização: Configuração do projeto com Kotlin e Maven.
  Lógica: Implementação sequencial dos exercícios de arrays, calculadora e sequências.
  POO: Desenvolvimento da estrutura da Biblioteca Virtual, incluindo data classes e membros.
  Android: Criação das aplicações "Hello World" e "Conversor", com ajustes de interface e lógica de conversão.

13. Dificuldades e Lições Aprendidas
Configuração de Ambiente: A tentativa inicial de usar o Android Studio para lógica pura e o NetBeans revelou-se ineficiente, sendo o IntelliJ IDEA com Maven a solução estável para os requisitos de POO.
Gestão de Threads: A ocorrência da exceção IllegalStateException (violação da EDT) foi uma lição fundamental sobre a arquitetura do Android Runtime (ART) e a necessidade de não bloquear a thread principal.
Interface Física: Ajustar o layout para evitar o notch da câmara realçou a importância de considerar as características do hardware no design de software móvel.

14. Melhorias Futuras
Implementação de uma chamada à API para obter o câmbio em tempo real.
Adição de suporte para múltiplas moedas (ex: GBP ou JPY) e persistência do histórico de conversões.

15. Divulgação de Uso de IA
De acordo com as normas de integridade académica do tutorial, declaro o seguinte uso de ferramentas de IA (Gemini):
  Seção MIP-1 (Obrigatório): Uso de IA para o planeamento e geração do código da App de Conversão (EUR/USD), conforme exigido para esta parte da avaliação.
  Redação Técnica: Apoio na estruturação e organização deste relatório Markdown.
  Apoio ao Estudo: Utilização de prompts para explicar conceitos complexos de POO e depuração de erros de compilação.
Confirmo que assumo total responsabilidade pela integridade de todo o código e estou apto a justificar a sua implementação técnica durante a defesa. 
