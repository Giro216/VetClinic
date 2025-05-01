const testPets = [
    {
        id: 1,
        name: "Барсик",
        species: "cat",
        breed: "Британский",
        age: 3
    },
    {
        id: 2,
        name: "Шарик",
        species: "dog",
        breed: "Дворняга",
        age: 5
    }
];


export async function getPets() {
    return new Promise(resolve => {
        setTimeout(() => resolve(testPets), 500);
    });
}
