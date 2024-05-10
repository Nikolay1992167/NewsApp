--liquibase formatted sql
--changeset Minich:2
INSERT INTO news.comments (id, text, username, news_id)
VALUES ('98484a32-71c6-44cf-8686-ca8f528d6ad5', 'The first in world.', 'Nikolay', 'acd9356c-07b9-422c-9140-95fd8d111bf6'),
       ('8cc4b223-d135-4a4e-b0c3-dc914036c539', 'Very-very good news.', 'Igor', 'acd9356c-07b9-422c-9140-95fd8d111bf6'),
       ('b3846354-f0a7-442d-b5d1-7aab13d69165', 'We will definitely explore space.', 'Oleg', 'acd9356c-07b9-422c-9140-95fd8d111bf6'),
       ('fab7c5d6-8fe4-4d03-8390-18bcbd81cf8c', 'It is a good car.', 'Yury', '166f30de-5356-4385-8d0d-eb1d37b07689'),
       ('df289596-c2e0-4064-8625-ae15015ab9fa', 'The right direction.', 'Pavel', '166f30de-5356-4385-8d0d-eb1d37b07689'),
       ('ff14480e-8c48-4e3e-b759-abae20063b8e', 'I would like such a car Xiaomi.', 'Sveta', '166f30de-5356-4385-8d0d-eb1d37b07689'),
       ('df825651-c7d0-49bb-b00d-e906e9144d30', 'Mercedes is the ideal of comfort.', 'Egor', 'e551e6c4-e1df-4084-8872-51c76e0b4801'),
       ('9b0f0069-a187-43e7-8465-69f7471820d3', 'The best cars are German as always.', 'Helena', 'e551e6c4-e1df-4084-8872-51c76e0b4801'),
       ('e70bb8df-b80a-4f3b-b002-a289d91f752f', 'Probably this car will cost a lot of money.', 'Dmitriy', 'e551e6c4-e1df-4084-8872-51c76e0b4801');
