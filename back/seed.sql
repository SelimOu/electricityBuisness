SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE medias;

TRUNCATE TABLE reservations;

TRUNCATE TABLE bornes;

TRUNCATE TABLE adresses;

TRUNCATE TABLE lieux;

TRUNCATE TABLE utilisateurs;

SET FOREIGN_KEY_CHECKS = 1;

-- insert users
INSERT INTO
    utilisateurs (
        id,
        nom_utilisateur,
        prenom,
        pseudo,
        mot_de_passe,
        role,
        adresse_mail,
        date_naissance,
        iban,
        vehicule,
        banni
    )
VALUES (
        1,
        'Dupont',
        'Alice',
        'alice',
        'password',
        'USER',
        'alice@example.com',
        '1990-01-01',
        NULL,
        NULL,
        0
    ),
    (
        2,
        'Martin',
        'Bob',
        'bob',
        'password',
        'USER',
        'bob@example.com',
        '1985-05-10',
        NULL,
        NULL,
        0
    );

-- insert lieux
INSERT INTO
    lieux (
        id,
        instructions,
        id_utilisateur
    )
VALUES (1, 'Parking center', 1),
    (2, 'wswwwsws', 2);

-- insert addresses
INSERT INTO
    adresses (
        id,
        nom_adresse,
        numero_et_rue,
        code_postal,
        ville,
        region,
        pays,
        complement,
        etage,
        id_lieu,
        id_utilisateur
    )
VALUES (
        1,
        'Parking Nord',
        '10 Rue de la Paix',
        '75001',
        'Paris',
        NULL,
        'France',
        NULL,
        NULL,
        1,
        1
    ),
    (
        2,
        'Supermarché Sud',
        '5 Avenue du Commerce',
        '69002',
        'Lyon',
        NULL,
        'France',
        NULL,
        NULL,
        2,
        2
    );

-- insert bornes
INSERT INTO
    bornes (
        id,
        nom_borne,
        coordgps,
        tarif,
        puissance,
        instruction,
        sur_pied,
        etat,
        occupee,
        id_lieu
    )
VALUES (
        1,
        'Borne A',
        '48.8566,2.3522',
        0.25,
        22,
        'Near mall',
        1,
        'OPERATIONAL',
        0,
        1
    ),
    (
        2,
        'Borne B',
        '45.7640,4.8357',
        0.30,
        11,
        'Zone C',
        0,
        'OPERATIONAL',
        0,
        2
    );

-- insert medias
INSERT INTO
    medias (
        id,
        id_borne,
        description,
        nom_media,
        taille,
        type,
        url
    )
VALUES (
        1,
        NULL,
        'Photo',
        'image.jpg',
        12345,
        'image',
        'http://example.com/image.jpg'
    ),
    (
        2,
        1,
        'Photo du parking',
        'photo_parking.jpg',
        102400,
        'image/jpeg',
        '/uploads/photo_parking.jpg'
    ),
    (
        3,
        2,
        'Photo du supermarché',
        'photo_station.jpg',
        204800,
        'image/jpeg',
        '/uploads/photo_station.jpg'
    );

-- insert reservations
INSERT INTO
    reservations (
        id,
        id_borne,
        id_utilisateur,
        date_debut,
        date_fin,
        etat,
        prix_minute_histo
    )
VALUES (
        1,
        1,
        1,
        NOW() - INTERVAL 1 DAY,
        NOW() - INTERVAL 1 DAY + INTERVAL 1 HOUR,
        'DONE',
        0.25
    );

SELECT 'seed complete' as status;