# Plan

Plaani puhul prioritiseerime vastavalt Glossaar.md roadmap failis kirjeldatud peamistele funktsionaalsustele.

## Setup

- [x] Loo uus repo + ligipääsud + protected main (#1)
- [x] Svelte starter project (#2)
- [x] Java Spring starter project (#3)
- [x] ise nullist authi ei teeks - oauth2 integratsioon BE (kas FE tahab ka midagi?) (#4)
- [x] mingisugune ORM lahendus üles seada (Java maailma standard on JPA?) (#5)
- [x] mingisugun svelte komponentide raamistik üles seada (btn, card, textfield, mida veel vaja?) bootstrap / tailwind / miski muu? (#6)
- [x] võiks mingi automaatne format-on-save lahendus olla BE (#7)
- [x] võiks mingi automaatne format-on-save lahendus olla FE (eslint + prettier? miskit tuleb ehk automaatselt kaasa) (#8)
- [x] kuidas lokaalselt jooksutame? (ka docker?) (#9)

## developemnt

- [x] mingi algne andmebaasi word tabelite koosseis (user >-< user_word >-< word_key >-< word_value(saab custom word puhul omaniku külge panna)) (#10)
- [x] User / auth endpointid (#11)
- BE põhilised word endpointid - toimetada saab vaid enda ressurssidega:
  - [x] POST words/{id} (#12)
  - [x] PATCH words/{id} (#13)
  - [x] DELETE words/{id} -  kustutab seose kasutaja ja sõna vahel. v.a. kui on custom word, siis kustutame sõna. (#14)
  - [x] GET words?filter=abs + pagination? (#15)
- FE vaated:
  - [x] Kirje lisamise/muutmise vaade (#16)
  - [x] Kirjete listivaade + filtrid + kustutamine + paginatsioon (#17)
  - [x] Quiz režiim vaade (#18)
- [x] põhilised quiz endpointid (algselt võib olla loogika ainult FE poolel?)
  - [x] mingi algne andmebaasi quiz tabelite koosseis (user_quiz - kasutajal oli nö seanss. user_quiz_result - seansside tulemused) (#19)
  - [x] POST quiz (seesmiselt tehakse words?filter=quiz&count=4 ja lisatakse, et milline on õige + luuakse user_quiz kirje ja sisestatakse user_quiz_result null väärtustega) (#20)
  - [x] POST quiz-results - APIs muudetakse vastavalt scorei, (FE saadab { userQuizId + mida valis õigesti, mida valesti }) (#21)
  - [x] GET words quiz filter arvesse võtma quiz-results tulemusi (küsida rohkem neid, millel skoor madal, aga alles siis kui küsimata sõnu ei ole). (#22)

## CI/CD

- [x] 1. Serverise docker (#23)
- [x] 2. Serverisse nginx (#24)
- [x] 3. Serverisse andmebaas püsti panna (eraldi konteiner) (#25)
- [x] FE build + ghrc deploy action + secretid (server + vajadusel GH) (#26)
- [x] BE build + ghrc BE deploy action + secretid (server + vajadusel GH) (#27)
- [x] Testide jooksutamine PRi avades, edukas run saab olema eeldus mergemiseks (#28)

## Design

- [x] vaadete wireframe'id luua vastavalt roadmapis väljatoodule (#29)
