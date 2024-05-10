--liquibase formatted sql
--changeset Minich:1
INSERT INTO news.news (id, title, text, id_author)
VALUES ('acd9356c-07b9-422c-9140-95fd8d111bf6', 'SpaceX updates settlement plans on Mars', 'SpaceX has announced new priorities for Starship that will pave the way for full and rapid reuse, which is a key factor for missions to Mars.', 'de1128e7-715c-448d-a068-d8273fa4c57d'),
       ('166f30de-5356-4385-8d0d-eb1d37b07689', 'Xiaomi releases the SU7 electric car', 'Xiaomi has said it can produce its popular full-size SU7 sedan in just 76 seconds, which has sparked interest in the automotive industry.', 'de1128e7-715c-448d-a068-d8273fa4c57d'),
       ('e551e6c4-e1df-4084-8872-51c76e0b4801', 'Mercedes-Benz E450 sets new standards for comfort', 'With a new six-cylinder engine and luxury in every detail, the Mercedes-Benz E450 raises the bar for premium sedans.', 'b3afa636-8006-42fe-961e-21ae926b3265');