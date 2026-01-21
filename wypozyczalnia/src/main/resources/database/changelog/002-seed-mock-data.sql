--liquibase formatted sql

--changeset github-copilot:002-seed-mock-data

-- Idempotent seed for demo/mock data.
-- NOTE: image_url values are intentionally empty strings so you can fill them later.

-- BOOKS
INSERT INTO books (id, title, author, isbn, published_year, genre, description, total_copies, available_copies, image_url)
SELECT '11111111-1111-1111-1111-111111111111', 'Wiedźmin: Ostatnie życzenie', 'Andrzej Sapkowski', '9788375780635', 1993, 'Fantastyka', 'Kultowy zbiór opowiadań wprowadzający w świat Geralta z Rivii. Wiedźmin to mistrz miecza i fachowiec od magii, który strzeże moralnej i biologicznej równowagi w cudownym i pięknym świecie. Jednak w tej krainie potwory czają się nie tylko w leśnych ostępach, ale także – a może przede wszystkim – w ludzkich sercach. Geralt stawia czoła strzydze, wampirowi, a nawet dżinowi, starając się zachować neutralność w świecie pełnym uprzedzeń, nienawiści i politycznych intryg. To tutaj poznajemy jego przeznaczenie, Ciri, oraz miłość jego życia, czarodziejkę Yennefer. Opowiadania te to nie tylko klasyczna fantasy, ale głęboka refleksja nad naturą zła, dobra i mniejszego zła, które często okazuje się najtrudniejszym wyborem. Sapowski z niezwykłym humorem i ironią dekonstruuje klasyczne baśnie, tworząc nową, mroczną i fascynującą jakość.', 5, 5, 'https://covers.openlibrary.org/b/isbn/9788375780635-L.jpg'
WHERE NOT EXISTS (SELECT 1 FROM books WHERE id = '11111111-1111-1111-1111-111111111111');

INSERT INTO books (id, title, author, isbn, published_year, genre, description, total_copies, available_copies, image_url)
SELECT '22222222-2222-2222-2222-222222222222', 'Solaris', 'Stanisław Lem', '9788308061215', 1961, 'Sci-Fi', 'Najsłynniejsze dzieło Stanisława Lema, należące do ścisłego kanonu światowej literatury science-fiction. Powieść opowiada o kontakcie z obcą inteligencją, która wymyka się wszelkim ludzkim schematom poznawczym. Kris Kelvin przybywa na stację badawczą orbitującą wokół planety Solaris, pokrytej tajemniczym, żywym oceanem. Szybko okazuje się, że ocean potrafi materializować najgłębiej skrywane, często traumatyczne wspomnienia badaczy. Kelvin musi zmierzyć się z widmem swojej zmarłej żony, Harey, która pojawia się na stacji jako "twór" oceanu. Solaris to nie tylko opowieść o kosmosie, ale przede wszystkim głęboki traktat filozoficzny o granicach ludzkiego poznania, o niemożności porozumienia się z czymś absolutnie Innym oraz o naturze człowieczeństwa, winy i odkupienia. Książka stawia pytania, na które nie ma prostych odpowiedzi.', 3, 3, 'https://covers.openlibrary.org/b/id/6853196-L.jpg'
WHERE NOT EXISTS (SELECT 1 FROM books WHERE id = '22222222-2222-2222-2222-222222222222');

INSERT INTO books (id, title, author, isbn, published_year, genre, description, total_copies, available_copies, image_url)
SELECT '33333333-3333-3333-3333-333333333333', 'Clean Code', 'Robert C. Martin', '9780132350884', 2008, 'IT', 'Biblia dla każdego programisty, który chce pisać kod nie tylko działający, ale także czytelny, łatwy w utrzymaniu i profesjonalny. Robert C. Martin (Wujek Bob) przedstawia zbiór zasad, wzorców i dobrych praktyk, które odróżniają rzemieślnika oprogramowania od zwykłego kley. Książka porusza tematy takie jak nazewnictwo zmiennych, tworzenie funkcji, unikanie powtórzeń (DRY), odpowiednie formatowanie, obsługa błędów czy pisanie testów jednostkowych. Znajdziesz tu mnóstwo przykładów kodu (głównie w Javie), pokazujących transformację "złego" kodu w "czysty". Autor przekonuje, że dbanie o jakość kodu to nie kwestia estetyki, ale ekonomicznej opłacalności projektu. Lektura obowiązkowa dla każdego, kto chce podnieść swoje umiejętności programistyczne na wyższy poziom i pracować w zespole bez generowania długu technologicznego.', 4, 4, 'https://covers.openlibrary.org/b/isbn/9780132350884-L.jpg'
WHERE NOT EXISTS (SELECT 1 FROM books WHERE id = '33333333-3333-3333-3333-333333333333');

INSERT INTO books (id, title, author, isbn, published_year, genre, description, total_copies, available_copies, image_url)
SELECT '44444444-4444-4444-4444-444444444444', 'Lalka', 'Bolesław Prus', '9788373271890', 1890, 'Powieść', 'Monumentalna powieść realistyczna ukazująca panoramę społeczną Warszawy drugiej połowy XIX wieku. Głównym wątkiem jest tragiczna miłość Stanisława Wokulskiego, zamożnego kupca o duszy romantyka, do arystokratki Izabeli Łęckiej. Wokulski, próbując zdobyć serce ukochanej, wchodzi na salony arystokracji, jednocześnie dostrzegając jej upadek i pustkę. Prus mistrzowsko kreśli portrety psychologiczne bohaterów, w tym starego subiekta Rzeckiego, którego pamiętnik stanowi drugi, nostalgiczny nurt narracji. "Lalka" to studium polskiego społeczeństwa w okresie przemian – zderzenie marzeń romantyków z trzeźwym pozytywizmem, upadek arystokracji i rodzący się kapitalizm. To także uniwersalna opowieść o idealizmie, rozczarowaniu i cenie, jaką płaci się za marzenia w zderzeniu z brutalną rzeczywistością.', 6, 6, 'https://covers.openlibrary.org/b/isbn/9788373271890-L.jpg'
WHERE NOT EXISTS (SELECT 1 FROM books WHERE id = '44444444-4444-4444-4444-444444444444');

INSERT INTO books (id, title, author, isbn, published_year, genre, description, total_copies, available_copies, image_url)
SELECT '55555555-5555-5555-5555-555555555555', 'Pan Tadeusz', 'Adam Mickiewicz', '9788304004469', 1834, 'Poezja', 'Polska epopeja narodowa spisana trzynastozgłoskowcem, będąca nostalgicznym obrazem szlacheckiej Polski, która odchodziła w przeszłość. Akcja toczy się na Litwie w latach 1811-1812, w przededniu wyprawy Napoleona na Rosję. Oś fabuły stanowi spór o zamek między dwoma rodami – Soplicami i Horeszkami, a także historia miłosna tytułowego Tadeusza i Zosi. W tle obserwujemy barwne życie szlachty: polowania, uczty, grzybobrania oraz zajazdy. Kluczową postacią jest tajemniczy ksiądz Robak, emisariusz polityczny, którego burzliwa przeszłość wiąże losy obu zwaśnionych rodzin. Utwór jest hymnem na cześć polskości, tradycji i przyrody, napisanym "ku pokrzepieniu serc" na emigracji. Pełen humoru, wzruszeń i opisów przyrody, "Pan Tadeusz" pozostaje najważniejszym dziełem polskiego romantyzmu, łączącym wątki narodowowyzwoleńcze z sielankowym obrazem "kraju lat dziecinnych".', 10, 10, 'https://covers.openlibrary.org/b/isbn/9788304004469-L.jpg'
WHERE NOT EXISTS (SELECT 1 FROM books WHERE id = '55555555-5555-5555-5555-555555555555');

INSERT INTO books (id, title, author, isbn, published_year, genre, description, total_copies, available_copies, image_url)
SELECT '66666666-6666-6666-6666-666666666666', 'Zbrodnia i kara', 'Fiodor Dostojewski', '9780140449136', 1866, 'Powieść psychologiczna', 'Jedno z najważniejszych dzieł literatury światowej, wnikliwe studium psychologiczne mordercy. Rodion Raskolnikow, były student żyjący w nędzy w Petersburgu, tworzy własną teorię o podziale ludzi na "zwykłych" i "niezwykłych", którzy mają prawo przekraczać granice moralne dla wyższych celów. Aby sprawdzić swoją teorię i poprawić swój los, zabija starą lichwiarkę. Czyn ten staje się początkiem jego koszmaru. Powieść nie jest kryminałem o poszukiwaniu sprawcy, lecz dramatem sumienia, walką rozumu z moralnością. Dostojewski mistrzowsko ukazuje rozpad psychiki bohatera, jego gorączkowe majaki, strach przed zdemaskowaniem i ostateczną drogę do duchowego odrodzenia poprzez cierpienie i miłość do Sonii, dziewczyny o czystym sercu zmuszonej do prostytucji. To poruszająca opowieść o winie, karze i odkupieniu.', 4, 2, 'https://covers.openlibrary.org/b/isbn/9780140449136-L.jpg'
WHERE NOT EXISTS (SELECT 1 FROM books WHERE id = '66666666-6666-6666-6666-666666666666');

INSERT INTO books (id, title, author, isbn, published_year, genre, description, total_copies, available_copies, image_url)
SELECT '77777777-7777-7777-7777-777777777777', 'Harry Potter i Kamień Filozoficzny', 'J.K. Rowling', '9788380082113', 1997, 'Fantastyka', 'Początek magicznej sagi, która podbiła serca milionów czytelników na całym świecie. Jedenastoletni Harry Potter, sierota wychowywany przez nieprzychylne wujostwo Dursleyów, dowiaduje się, że jest czarodziejem. Trafia do Szkoły Magii i Czarodziejstwa w Hogwarcie, gdzie odkrywa prawdę o swoim pochodzeniu i tragicznej śmierci rodziców z rąk potężnego czarnoksiężnika Voldemorta. Harry zdobywa wiernych przyjaciół – Rona i Hermionę – z którymi przeżywa niesamowite przygody, uczy się latania na miotle i gra w Quidditcha. Jednak nad Hogwartem zbierają się ciemne chmury. Harry musi stawić czoła zagadce Kamienia Filozoficznego i po raz pierwszy zmierzyć się z siłami zła, które zagrażają nie tylko jemu, ale całemu magicznemu światu. To opowieść o przyjaźni, odwadze i walce dobra ze złem, pełna humoru i niezwykłej wyobraźni.', 8, 0, 'https://covers.openlibrary.org/b/isbn/9788380082113-L.jpg'
WHERE NOT EXISTS (SELECT 1 FROM books WHERE id = '77777777-7777-7777-7777-777777777777');

INSERT INTO books (id, title, author, isbn, published_year, genre, description, total_copies, available_copies, image_url)
SELECT '88888888-8888-8888-8888-888888888888', 'Władca Pierścieni: Drużyna Pierścienia', 'J.R.R. Tolkien', '9788328700789', 1954, 'Fantastyka', 'Pierwsza część monumentalnej trylogii, która zdefiniowała gatunek high fantasy. W spokojnym Shire hobbit Frodo Baggins dziedziczy po wuju tajemniczy pierścień, który okazuje się Jedynym Pierścieniem, narzędziem władzy absolutnej, stworzonym przez Mrocznego Władcę Saurona. Aby ocalić Śródziemie przed wieczną ciemnością, Frodo musi wyruszyć w niebezpieczną podróż do Góry Przeznaczenia w Mordorze, jedynego miejsca, gdzie Pierścień może zostać zniszczony. Towarzyszy mu Drużyna Pierścienia: czarodziej Gandalf, ludzie Aragorn i Boromir, elf Legolas, krasnolud Gimli oraz wierni przyjaciele hobbici – Sam, Merry i Pippin. Wyprawa ta wystawi ich na próbę, zmusi do walki z orkami, upiorami pierścienia i własnymi słabościami. Epicka opowieść o poświęceniu, przyjaźni i walce nawet w obliczu beznadziejnej przewagi zła.', 5, 3, 'https://covers.openlibrary.org/b/isbn/9788328700789-L.jpg'
WHERE NOT EXISTS (SELECT 1 FROM books WHERE id = '88888888-8888-8888-8888-888888888888');

INSERT INTO books (id, title, author, isbn, published_year, genre, description, total_copies, available_copies, image_url)
SELECT '99999999-9999-9999-9999-999999999999', 'Rok 1984', 'George Orwell', '9780451524935', 1949, 'Dystopia', 'Przerażająca wizja totalitarnej przyszłości, w której jednostka jest całkowicie podporządkowana państwu. Świat podzielony jest na trzy mocarstwa, a w Oceanii panuje Wielki Brat, który widzi wszystko. Partia kontroluje nie tylko działania, ale i myśli obywateli (Myślozbrodnia), historię jest nieustannie przepisywana na nowo, a język (Nowomowa) redukowany, by uniemożliwić wyrażanie buntu. Główny bohater, Winston Smith, pracownik Ministerstwa Prawdy, zaczyna dostrzegać kłamstwa systemu i podejmuje próbę cichego oporu, zakochując się w Julii. Ich zakazana miłość jest aktem buntu w świecie bez uczuć. Powieść jest genialnym ostrzeżeniem przed fanatyzmem, inwigilacją i manipulacją prawdą. Pojęcia takie jak "Wielki Brat", "policja myśli" czy "dwójmyślenie" na stałe weszły do naszego języka, a wizja Orwella pozostaje niepokojąco aktualna.', 7, 6, 'https://covers.openlibrary.org/b/isbn/9780451524935-L.jpg'
WHERE NOT EXISTS (SELECT 1 FROM books WHERE id = '99999999-9999-9999-9999-999999999999');

INSERT INTO books (id, title, author, isbn, published_year, genre, description, total_copies, available_copies, image_url)
SELECT '10101010-1010-1010-1010-101010101010', 'Mistrz i Małgorzata', 'Michaił Bułhakow', '9780141180144', 1967, 'Klasyka', 'Arcydzieło literatury rosyjskiej, łączące satyrę na radziecką rzeczywistość lat 30. z wątkami biblijnymi i fantastycznymi. Do ateistycznej Moskwy przybywa Szatan pod postacią profesora Wolanda wraz ze swoją świtą (m.in. ogromnym kotem Behemotem), wywołując chaos i obnażając hipokryzję, chciwość i małość mieszkańców stolicy. Równolegle poznajemy historię Mistrza, pisarza zniszczonego przez cenzurę za napisanie powieści o Poncjuszu Piłacie, oraz jego ukochanej Małgorzaty, która gotowa jest na pakt z diabłem, by odzyskać ukochanego. Powieść przeplata wątki moskiewskie z historią procesu Jezusa w Jerozolimie, stawiając fundamentalne pytania o dobro i zło, tchórzostwo i odwagę, wiarę i miłość, która jest silniejsza niż śmierć. To wielowarstwowa, pełna magii i humoru opowieść, która wymyka się jednoznacznym interpretacjom.', 4, 4, 'https://covers.openlibrary.org/b/isbn/9780141180144-L.jpg'
WHERE NOT EXISTS (SELECT 1 FROM books WHERE id = '10101010-1010-1010-1010-101010101010');

-- MEMBERS
INSERT INTO members (id, first_name, last_name, email, active)
SELECT 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Jan', 'Kowalski', 'jan.kowalski@example.com', true
WHERE NOT EXISTS (SELECT 1 FROM members WHERE id = 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa');

INSERT INTO members (id, first_name, last_name, email, active)
SELECT 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Anna', 'Nowak', 'anna.nowak@example.com', true
WHERE NOT EXISTS (SELECT 1 FROM members WHERE id = 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb');

INSERT INTO members (id, first_name, last_name, email, active)
SELECT 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'Piotr', 'Zieliński', 'piotr.zielinski@example.com', false
WHERE NOT EXISTS (SELECT 1 FROM members WHERE id = 'cccccccc-cccc-cccc-cccc-cccccccccccc');

INSERT INTO members (id, first_name, last_name, email, active)
SELECT 'd1d1d1d1-d1d1-d1d1-d1d1-d1d1d1d1d1d1', 'Maria', 'Wiśniewska', 'maria.wisniewska@example.com', true
WHERE NOT EXISTS (SELECT 1 FROM members WHERE id = 'd1d1d1d1-d1d1-d1d1-d1d1-d1d1d1d1d1d1');

INSERT INTO members (id, first_name, last_name, email, active)
SELECT 'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1', 'Krzysztof', 'Krawczyk', 'krzysztof.krawczyk@example.com', true
WHERE NOT EXISTS (SELECT 1 FROM members WHERE id = 'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1');

INSERT INTO members (id, first_name, last_name, email, active)
SELECT 'f1f1f1f1-f1f1-f1f1-f1f1-f1f1f1f1f1f1', 'Barbara', 'Sienkiewicz', 'barbara.sienkiewicz@example.com', true
WHERE NOT EXISTS (SELECT 1 FROM members WHERE id = 'f1f1f1f1-f1f1-f1f1-f1f1-f1f1f1f1f1f1');

INSERT INTO members (id, first_name, last_name, email, active)
SELECT '71717171-7171-7171-7171-717171717171', 'Tomasz', 'Lis', 'tomasz.lis@example.com', true
WHERE NOT EXISTS (SELECT 1 FROM members WHERE id = '71717171-7171-7171-7171-717171717171');

INSERT INTO members (id, first_name, last_name, email, active)
SELECT '81818181-8181-8181-8181-818181818181', 'Ewa', 'Bem', 'ewa.bem@example.com', true
WHERE NOT EXISTS (SELECT 1 FROM members WHERE id = '81818181-8181-8181-8181-818181818181');

-- LOANS
INSERT INTO loans (id, book_id, member_id, loan_date, due_date, return_date, status)
SELECT 'dddddddd-dddd-dddd-dddd-dddddddddddd',
       '11111111-1111-1111-1111-111111111111',
       'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
       CURRENT_DATE - INTERVAL '10 days',
       CURRENT_DATE + INTERVAL '10 days',
       NULL,
       'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM loans WHERE id = 'dddddddd-dddd-dddd-dddd-dddddddddddd');

INSERT INTO loans (id, book_id, member_id, loan_date, due_date, return_date, status)
SELECT 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
       '22222222-2222-2222-2222-222222222222',
       'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
       CURRENT_DATE - INTERVAL '30 days',
       CURRENT_DATE - INTERVAL '5 days',
       NULL,
       'OVERDUE'
WHERE NOT EXISTS (SELECT 1 FROM loans WHERE id = 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee');

INSERT INTO loans (id, book_id, member_id, loan_date, due_date, return_date, status)
SELECT 'f1f1f1f1-f1f1-f1f1-f1f1-f1f1f1f1f1f1',
       '44444444-4444-4444-4444-444444444444',
       'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
       CURRENT_DATE - INTERVAL '2 days',
       CURRENT_DATE + INTERVAL '28 days',
       NULL,
       'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM loans WHERE id = 'f1f1f1f1-f1f1-f1f1-f1f1-f1f1f1f1f1f1');

INSERT INTO loans (id, book_id, member_id, loan_date, due_date, return_date, status)
SELECT '1a1a1a1a-1a1a-1a1a-1a1a-1a1a1a1a1a1a',
       '77777777-7777-7777-7777-777777777777',
       'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
       CURRENT_DATE - INTERVAL '60 days',
       CURRENT_DATE - INTERVAL '30 days',
       CURRENT_DATE - INTERVAL '35 days',
       'RETURNED'
WHERE NOT EXISTS (SELECT 1 FROM loans WHERE id = '1a1a1a1a-1a1a-1a1a-1a1a-1a1a1a1a1a1a');

INSERT INTO loans (id, book_id, member_id, loan_date, due_date, return_date, status)
SELECT '1b1b1b1b-1b1b-1b1b-1b1b-1b1b1b1b1b1b',
       '99999999-9999-9999-9999-999999999999',
       'cccccccc-cccc-cccc-cccc-cccccccccccc',
       CURRENT_DATE - INTERVAL '40 days',
       CURRENT_DATE - INTERVAL '10 days',
       NULL,
       'OVERDUE'
WHERE NOT EXISTS (SELECT 1 FROM loans WHERE id = '1b1b1b1b-1b1b-1b1b-1b1b-1b1b1b1b1b1b');
