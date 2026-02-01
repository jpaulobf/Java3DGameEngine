# Java 3D Game Engine

Uma engine de jogo 3D simples desenvolvida em Java puro, utilizando renderização via software (sem bibliotecas gráficas externas como OpenGL para o pipeline principal).

## Controles

*   **W / S**: Mover a nave verticalmente (Eixo Y).
*   **A / D**: Mover a nave lateralmente (Eixo X).
*   **Espaço**: Acelerar (Boost de velocidade com efeito visual de rastro nas estrelas e tremor de câmera).
*   **J**: Atirar lasers.
*   **F**: Alternar entre iluminação Flat (facetada) e Gouraud (suave).

## Mecânicas

*   **Navegação**: A nave se desloca automaticamente para frente no eixo Z. O jogador controla o posicionamento na tela.
*   **Câmera**: A câmera segue a nave em terceira pessoa, com efeitos de inclinação e tremor baseados na velocidade.
*   **Renderização**: Pipeline 3D customizado com suporte a Z-Buffer, Clipping, Iluminação Dinâmica e Rasterização de Polígonos.
*   **Ambiente**: Campo de estrelas infinito gerado proceduralmente.