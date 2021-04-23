-- webclient : ghost:medic    /    Z2hvc3Q6bWVkaWM=
-- android : valkyrie:wraith    /    dmFsa3lyaWU6d3JhaXRo
-- ios: arbiter:corsair    /    YXJiaXRlcjpjb3JzYWly
-- pcclient : vulture:goliath    /    dnVsdHVyZTpnb2xpYXRo
INSERT INTO oauth_client_details (client_id, client_secret, scope, authorized_grant_types, authorities, access_token_validity,refresh_token_validity)
VALUES
  ('refinery','{bcrypt}$2a$10$Hb2OhI1KJEK10uRLThNsFOO7aKiO/2x1LlHKFgB0WkSkCStPebQsi','trust-client','client_credentials,refresh_token', 'ROLE_CLIENT',3600,86400),
  ('bunker','{bcrypt}$2a$10$Hb2OhI1KJEK10uRLThNsFOO7aKiO/2x1LlHKFgB0WkSkCStPebQsi','trust-client','client_credentials,refresh_token', 'ROLE_CLIENT',3600,86400),
  ('overlord','{bcrypt}$2a$10$ekKP4aqDJlUbOIR8EEJZpemg20S/RyasNOWCPQGsjXbXWKvlM61zm','trust-client','client_credentials,refresh_token', 'ROLE_CLIENT',3600,86400),
  ('mutalisk','{bcrypt}$2a$10$ekKP4aqDJlUbOIR8EEJZpemg20S/RyasNOWCPQGsjXbXWKvlM61zm','trust-client','client_credentials,refresh_token', 'ROLE_CLIENT',3600,86400),
  ('hydralisk','{bcrypt}$2a$10$ekKP4aqDJlUbOIR8EEJZpemg20S/RyasNOWCPQGsjXbXWKvlM61zm','trust-client','client_credentials,refresh_token', 'ROLE_CLIENT',3600,86400),
  ('sunkencolony','{bcrypt}$2a$10$ekKP4aqDJlUbOIR8EEJZpemg20S/RyasNOWCPQGsjXbXWKvlM61zm','trust-client','client_credentials,refresh_token', 'ROLE_CLIENT',3600,86400),
  ('shuttle','{bcrypt}$2a$10$WAv9q8NZTaZXI3sRJIzoZem8K4sAc5HpYxzP5sG8eK84JkKllsfUC','trust-client','client_credentials,refresh_token', 'ROLE_CLIENT',3600,86400);
  