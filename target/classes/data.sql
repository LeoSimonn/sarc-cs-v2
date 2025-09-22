-- Inserir Students
INSERT INTO student (name, registration_number) VALUES
  ('Ana Silva', 'R001'),
  ('Bruno Costa', 'R002'),
  ('Carla Mendes', 'R003')
ON CONFLICT DO NOTHING;
    
-- Inserir Subjects
INSERT INTO subject (subject_code, subject_name, schedule) VALUES
  ('MAT101', 'Matemática Básica', 'Seg 10:00'),
  ('HIS201', 'História Geral', 'Qua 14:00'),
  ('BIO301', 'Biologia Avançada', 'Sex 08:00')
ON CONFLICT DO NOTHING;
