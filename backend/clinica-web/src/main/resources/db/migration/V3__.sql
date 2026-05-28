
ALTER TABLE animais
    ADD imagem BYTEA;

ALTER TABLE diaria_internacao
ALTER COLUMN peso_no_dia TYPE DECIMAL USING (peso_no_dia::DECIMAL);

ALTER TABLE registro_prontuario
ALTER COLUMN peso_no_dia TYPE DECIMAL USING (peso_no_dia::DECIMAL);

ALTER TABLE administracoes_medicamentos
ALTER COLUMN quantidade_administrada TYPE DECIMAL USING (quantidade_administrada::DECIMAL);