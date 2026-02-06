# Plan

Plaani puhul prioritiseerime vastavalt Glossaar.md roadmap failis kirjeldatud peamistele funktsionaalsustele.

## Setup

- [ ] Loo uus repo + ligipääsud + protected main [#1]
- [ ] Svelte starter project [#2]
- [ ] Java Spring starter project [#3]
- [ ] ise nullist authi ei teeks - oauth2 integratsioon BE (kas FE tahab ka midagi?) [#4]
- [ ] mingisugune ORM lahendus üles seada (Java maailma standard on JPA?) [#5]
- [ ] mingisugune light-weight svelte komponentide raamistik üles seada (btn, card, textfield, mida veel vaja?) bootstrap / tailwind / miski muu? [#6]
- [ ] võiks mingi automaatne format-on-save lahendus olla BE [#7]
- [ ] võiks mingi automaatne format-on-save lahendus olla FE (eslint + prettier? miskit tuleb ehk automaatselt kaasa) [#8]
- [ ] kuidas lokaalselt jooksutame? (ka docker?) [#9]

## developemnt

- [ ] mingi algne andmebaasi word tabelite koosseis (user >-< user_word >-< word_key >-< word_value(saab custom word puhul omaniku külge panna)) [#10]
- [ ] User / auth endpointid [#11]
- BE põhilised word endpointid - toimetada saab vaid enda ressurssidega:
  - [ ] POST words/{id} [#12]
  - [ ] PATCH words/{id} [#13]
  - [ ] DELETE words/{id} -  kustutab seose kasutaja ja sõna vahel. v.a. kui on custom word, siis kustutame sõna. [#14]
  - [ ] GET words?filter=abs + pagination? [#15]
- FE vaated:
  - [ ] Kirje lisamise/muutmise vaade [#16]
  - [ ] Kirjete listivaade + filtrid + kustutamine + paginatsioon [#17]
  - [ ] Quiz režiim vaade [#18]
- [ ] põhilised quiz endpointid (algselt võib olla loogika ainult FE poolel?)
  - [ ] mingi algne andmebaasi quiz tabelite koosseis (user_quiz - kasutajal oli nö seanss. user_quiz_result - seansside tulemused) [#19]
  - [ ] POST quiz (seesmiselt tehakse words?filter=quiz&count=4 ja lisatakse, et milline on õige + luuakse user_quiz kirje ja sisestatakse user_quiz_result null väärtustega) [#20]
  - [ ] POST quiz-results - APIs muudetakse vastavalt scorei, (FE saadab { userQuizId + mida valis õigesti, mida valesti }) [#21]
  - [ ] GET words quiz filter arvesse võtma quiz-results tulemusi (küsida rohkem neid, millel skoor madal, aga alles siis kui küsimata sõnu ei ole). [#22]

## CI/CD

- [ ] 1. Serverise docker [#23]
- [ ] 2. Serverisse nginx [#24]
- [ ] 3. Serverisse andmebaas püsti panna (saab sama psql instance kasutada mis borsibaar) [#25]
- [ ] FE build + ghrc deploy action + secretid (server + vajadusel GH) [#26]
- [ ] BE build + ghrc BE deploy action + secretid (server + vajadusel GH) [#27]
- [ ] Testide jooksutamine PRi avades, edukas run saab olema eeldus mergemiseks [#28]

## Design

- [ ] vaadete wireframe'id luua vastavalt roadmapis väljatoodule [#29]
