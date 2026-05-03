---
theme: seriph

title: Glossaar

layout: cover

transition: slide-left

---

# Glossaar

team-01

<br>
Glossaar - tundmatuid sõnu seletav sõnastik.
<br>
Glossary - a list consisting of pairs of entries, with a term and its definition making up each pair.

---

# Idee

Isiklik mure - asjad meelde ei jää.

<br>

<div v-click>
    <b>Probleem:</b> Loed ja mõtestad lahti mingeid termineid aga järgmine kord on meelest läinud.
</div>


<div v-click>
    <b>Lahendus:</b> Veebirakendus, mis on toetavaks abiliseks sõnavara laiendamisel.
</div>

---

# Kellele?

<br>

<ul>
<li v-click>
Inimene kes tunneb, et tahab oma sõnavara tähendada eesti või inglise keeles.
</li>

<li v-click>
Inimene kes tunneb, et kuigi on enda jaoks asja lahti mõtestandu, ei pruugi see püsimällu jääda.
</li>
</ul>

---

# Rakenduse peamine funktsionaalsus

<br>

- Sõna ja kirjelduse lisamine
- Sõnale välisest allikast kirjelduse saamine
- Sõnade õppimine läbi nö 'quiz' režiimi

---

# Tehniline ülevaade (1/2)

<script setup>
    import lz from 'lz-string'
    import schemaRaw from './../architecture.mermaid?raw'
    const schema = lz.compressToBase64(schemaRaw)
</script>

<Mermaid :codeLz="schema" :scale="0.55" />

---

# Tehniline ülevaade (2/2)

<script setup>
    import lz from 'lz-string'
    import schemaRaw from './../db_schema.mermaid?raw'
    const schema = lz.compressToBase64(schemaRaw)
</script>

<Mermaid :codeLz="schema" :scale="0.53" />

---

# Protsess

- iganädalane kohtumine mentoriga
- iganädalane sprint planning / retro (läbiviija roteerus)
- GH kanban board
- GH issued + mahud

---

# Tiim

<script setup>
    const list = [
        { name: 'Katri', roles: ['Full Stack', 'Meetings'], avatar: 'https://cdn.discordapp.com/avatars/758896311889297410/53cf44490f6fd0463908f71cc9057ad2.webp?size=96' },
        { name: 'Liisi', roles: ['Full Stack', 'Meetings'], avatar: 'https://cdn.discordapp.com/avatars/1026401921650675762/f7f2e9361e8a54ce6e72580ac7b967af.webp?size=160' },
        { name: 'Ivo', roles: ['Full Stack', 'Meetings'], avatar: 'https://cdn.discordapp.com/avatars/606462060536266769/bac1996fb34ae4dc959cf29b839c05e3.webp?size=96' },
        { name: 'Anastasia', roles: ['Full Stack', 'Meetings'], avatar: 'https://cdn.discordapp.com/avatars/1458092956543090816/fdecdaad5bf3b493064e89ea8668a4f0.webp?size=96' },
        { name: 'Kenert', roles: ['Full Stack', 'DevOps', 'Team Lead'], avatar: 'https://avatars.githubusercontent.com/u/46687296?v=4' },
    ];
</script>

<div class="flex gap-8 mt-4">
  <div v-for="person in list" :key="person.name" class="flex flex-col items-center gap-2 text-center">
    <img :src="person.avatar" :alt="person.name" class="w-20 h-20 rounded-full" />
    <span class="font-bold">{{ person.name }}</span>
    <div v-click class="flex flex-col gap-1">
      <span v-for="role in person.roles" :key="role" class="text-xs opacity-70">{{ role }}</span>
    </div>
  </div>
</div>


---

# Rakenduse video

<!-- TODO: add correct ID -->
<Youtube id="Km6CC1ujJPg" width="90%" height="90%" />

---

# Aitäh