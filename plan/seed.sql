DELETE FROM delays;
DELETE FROM tickets;
DELETE FROM bookings;
DELETE FROM users;
DELETE FROM schedule_stops;
DELETE FROM schedules;
DELETE FROM trains;
DELETE FROM route_stops;
DELETE FROM routes;
DELETE FROM stations;

SELECT setval(pg_get_serial_sequence('stations',       'id'), 1, false);
SELECT setval(pg_get_serial_sequence('routes',         'id'), 1, false);
SELECT setval(pg_get_serial_sequence('route_stops',    'id'), 1, false);
SELECT setval(pg_get_serial_sequence('trains',         'id'), 1, false);
SELECT setval(pg_get_serial_sequence('schedules',      'id'), 1, false);
SELECT setval(pg_get_serial_sequence('schedule_stops', 'id'), 1, false);
SELECT setval(pg_get_serial_sequence('users',          'id'), 1, false);
SELECT setval(pg_get_serial_sequence('bookings',       'id'), 1, false);
SELECT setval(pg_get_serial_sequence('tickets',        'id'), 1, false);
SELECT setval(pg_get_serial_sequence('delays',         'id'), 1, false);

INSERT INTO stations (id, city) VALUES
(1, 'Bucuresti Nord'),
(2, 'Ploiesti Sud'),
(3, 'Sinaia'),
(4, 'Brasov'),
(5, 'Sibiu'),
(6, 'Cluj-Napoca');

INSERT INTO routes (id, name) VALUES
(1, 'Bucuresti - Brasov'),
(2, 'Brasov - Cluj-Napoca');

INSERT INTO route_stops (id, route_id, station_id, stop_number) VALUES
(1, 1, 1, 1),
(2, 1, 2, 2),
(3, 1, 3, 3),
(4, 1, 4, 4),
(5, 2, 4, 1),
(6, 2, 5, 2),
(7, 2, 6, 3);

INSERT INTO trains (id, train_number, number_of_seats) VALUES
(1, 'IR 1581', 200),
(2, 'IR 1582', 180),
(3, 'IC 521',  300),
(4, 'IR 9999',   2);

INSERT INTO schedules (id, route_id, train_id) VALUES
(1, 1, 1),
(2, 1, 2),
(3, 2, 3),
(4, 1, 4);

INSERT INTO schedule_stops (id, schedule_id, route_stop_id, arrival_time, departure_time) VALUES
(1,  1, 1, '2026-05-15 08:00:00', '2026-05-15 08:00:00'),
(2,  1, 2, '2026-05-15 09:00:00', '2026-05-15 09:05:00'),
(3,  1, 3, '2026-05-15 10:15:00', '2026-05-15 10:20:00'),
(4,  1, 4, '2026-05-15 11:30:00', '2026-05-15 11:30:00'),
(5,  2, 1, '2026-05-15 16:00:00', '2026-05-15 16:00:00'),
(6,  2, 2, '2026-05-15 17:00:00', '2026-05-15 17:05:00'),
(7,  2, 3, '2026-05-15 18:15:00', '2026-05-15 18:20:00'),
(8,  2, 4, '2026-05-15 19:30:00', '2026-05-15 19:30:00'),
(9,  3, 5, '2026-05-15 12:00:00', '2026-05-15 12:00:00'),
(10, 3, 6, '2026-05-15 14:30:00', '2026-05-15 14:35:00'),
(11, 3, 7, '2026-05-15 17:00:00', '2026-05-15 17:00:00'),
(12, 4, 1, '2026-05-16 08:00:00', '2026-05-16 08:00:00'),
(13, 4, 2, '2026-05-16 09:00:00', '2026-05-16 09:05:00'),
(14, 4, 3, '2026-05-16 10:15:00', '2026-05-16 10:20:00'),
(15, 4, 4, '2026-05-16 11:30:00', '2026-05-16 11:30:00');

INSERT INTO users (id, username, email, password_hash, role) VALUES
(1, 'admin',              'trainTicketingApp@gmail.com',      '$2b$10$2Z8SG9hhNbvPLcA.FeyPl.gQveQ5/FNnJc1QzZxjaZ/pAZQpZMl2.', 'ADMIN'),
(2, 'sofia.balaci',       'sofiabalaci02@gmail.com',          '$2b$10$2Z8SG9hhNbvPLcA.FeyPl.gQveQ5/FNnJc1QzZxjaZ/pAZQpZMl2.', 'CLIENT'),
(3, 'cristian.alexutan',  'cristianalexutan2005@gmail.com',   '$2b$10$2Z8SG9hhNbvPLcA.FeyPl.gQveQ5/FNnJc1QzZxjaZ/pAZQpZMl2.', 'CLIENT');

INSERT INTO bookings (id, user_id) VALUES
(1, 2),
(2, 3),
(3, 2),
(4, 3);

INSERT INTO tickets (id, booking_id, departure_schedule_stop_id, arrival_schedule_stop_id) VALUES
(1, 1, 1, 4),
(2, 1, 1, 4),
(3, 2, 1, 3),
(4, 3, 12, 15),
(5, 4, 12, 15);

INSERT INTO delays (id, schedule_id, from_schedule_stop_id, delay_minutes) VALUES
(1, 1, 2, 20);

SELECT setval(pg_get_serial_sequence('stations',       'id'), (SELECT MAX(id) FROM stations));
SELECT setval(pg_get_serial_sequence('routes',         'id'), (SELECT MAX(id) FROM routes));
SELECT setval(pg_get_serial_sequence('route_stops',    'id'), (SELECT MAX(id) FROM route_stops));
SELECT setval(pg_get_serial_sequence('trains',         'id'), (SELECT MAX(id) FROM trains));
SELECT setval(pg_get_serial_sequence('schedules',      'id'), (SELECT MAX(id) FROM schedules));
SELECT setval(pg_get_serial_sequence('schedule_stops', 'id'), (SELECT MAX(id) FROM schedule_stops));
SELECT setval(pg_get_serial_sequence('users',          'id'), (SELECT MAX(id) FROM users));
SELECT setval(pg_get_serial_sequence('bookings',       'id'), (SELECT MAX(id) FROM bookings));
SELECT setval(pg_get_serial_sequence('tickets',        'id'), (SELECT MAX(id) FROM tickets));
SELECT setval(pg_get_serial_sequence('delays',         'id'), (SELECT MAX(id) FROM delays));
