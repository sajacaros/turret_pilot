-- webclient : ghost:medic    /    Z2hvc3Q6bWVkaWM=
-- android : valkyrie:wraith    /    dmFsa3lyaWU6d3JhaXRo
-- ios: arbiter:corsair    /    YXJiaXRlcjpjb3JzYWly
-- pcclient : vulture:goliath    /    dnVsdHVyZTpnb2xpYXRo
INSERT INTO oauth_client_details (client_id, client_secret, scope, authorized_grant_types, authorities, access_token_validity,refresh_token_validity)
VALUES
  ('ghost','{bcrypt}$2a$10$0MS7xjBtNUnmCIpIJe72m.Y/EVF.3wbU2YbBYDGqpc5Vf5dw3bLfe','trust-client','password,refresh_token', 'ROLE_CLIENT',3600,86400),
  ('valkyrie','{bcrypt}$2a$10$wAlyAkkwcCEbZhRvvWUap.rMuXo1eGkrWcZMJNtMf9ATuX6sC6siK','trust-client','password,refresh_token', 'ROLE_CLIENT',3600,86400),
  ('arbiter','{bcrypt}$2a$10$91Gf3BslI.nhxGmRbF3MNu83KZC.UKxprjUZrgBb.TlhyModDIHFS','trust-client','password,refresh_token', 'ROLE_CLIENT',3600,86400),
  ('vulture','{bcrypt}$2a$10$UIQXPZ0fvojPHL3va.ptium.lDx7d94W8b.4o4J2GNGwB.Narsp12','trust-client','password,refresh_token', 'ROLE_CLIENT',3600,86400),
  ('zergling','{bcrypt}$2a$10$3KiDEg59WQ8Jo0e0gCyOveQw6DfAc/6zcQ5UZdWyF4g3ugVq1B8U2','trust-client','password,refresh_token', 'ROLE_CLIENT',60,120),
  ('commandCenter','{bcrypt}$2a$10$5yWQhrcOBfsByozIkZ6CVeZkPQB5zPwKOkQ/Mp9mB27jWYLooP5JG','trust-client','client_credentials,refresh_token', 'ROLE_CLIENT',3600,86400),
  ('cyberneticsCore','{bcrypt}$2a$10$WAv9q8NZTaZXI3sRJIzoZem8K4sAc5HpYxzP5sG8eK84JkKllsfUC','trust-client','client_credentials,refresh_token', 'ROLE_CLIENT',3600,86400),
  ('templarArchives','{bcrypt}$2a$10$WAv9q8NZTaZXI3sRJIzoZem8K4sAc5HpYxzP5sG8eK84JkKllsfUC','trust-client','client_credentials,refresh_token', 'ROLE_CLIENT',3600,86400),
  ('nydusCanal','{bcrypt}$2a$10$ekKP4aqDJlUbOIR8EEJZpemg20S/RyasNOWCPQGsjXbXWKvlM61zm','trust-client','client_credentials,refresh_token', 'ROLE_CLIENT',3600,86400),
  ('batteryShield','{bcrypt}$2a$10$WAv9q8NZTaZXI3sRJIzoZem8K4sAc5HpYxzP5sG8eK84JkKllsfUC','trust-client','client_credentials,refresh_token', 'ROLE_CLIENT',3600,86400),
  ('comsatStation','{bcrypt}$2a$10$5yWQhrcOBfsByozIkZ6CVeZkPQB5zPwKOkQ/Mp9mB27jWYLooP5JG','commandCenter','password,refresh_token', 'ROLE_CLIENT',3600,86400),
  ('spawningPool','{bcrypt}$2a$10$ekKP4aqDJlUbOIR8EEJZpemg20S/RyasNOWCPQGsjXbXWKvlM61zm','trust-client','client_credentials,refresh_token', 'ROLE_CLIENT',3600,86400),
  ('pylon','{bcrypt}$2a$10$WAv9q8NZTaZXI3sRJIzoZem8K4sAc5HpYxzP5sG8eK84JkKllsfUC','trust-client','client_credentials,refresh_token', 'ROLE_CLIENT',3600,86400),
  ('queensNest','{bcrypt}$2a$10$ekKP4aqDJlUbOIR8EEJZpemg20S/RyasNOWCPQGsjXbXWKvlM61zm','trust-client','client_credentials,refresh_token', 'ROLE_CLIENT',3600,86400),
  ('nexus','{bcrypt}$2a$10$WAv9q8NZTaZXI3sRJIzoZem8K4sAc5HpYxzP5sG8eK84JkKllsfUC','trust-client','client_credentials,refresh_token', 'ROLE_CLIENT',3600,86400),
  ('arbiterTribunal','{bcrypt}$2a$10$WAv9q8NZTaZXI3sRJIzoZem8K4sAc5HpYxzP5sG8eK84JkKllsfUC','trust-client','client_credentials,refresh_token', 'ROLE_CLIENT',3600,86400),
  ('armory','{bcrypt}$2a$10$Hb2OhI1KJEK10uRLThNsFOO7aKiO/2x1LlHKFgB0WkSkCStPebQsi','trust-client','client_credentials,refresh_token', 'ROLE_CLIENT',3600,86400);
