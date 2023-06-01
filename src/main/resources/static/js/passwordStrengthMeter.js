

/* Password Strength code, copied from register.html */
function checkPasswordStrength(elem) {

    const bar = document.querySelector(".password-strength-meter .bar");
    var password = document.getElementById(elem.id).value;
    const minChar= document.getElementById("password-min-characters");
    const hasLetterCase= document.getElementById("password-letter-case");
    const hasNumber= document.getElementById("password-numbers");
    const hasSymbol= document.getElementById("password-symbols");
    const ownDetails= document.getElementById("password-user-details");
    password=password.trim()
    document.getElementById(elem.id).value = password;

    if (password) {
        const minCharImg = minChar.querySelector("img");
        const letterCaseImg = hasLetterCase.querySelector("img");
        const numberImg=hasNumber.querySelector("img");
        const symbolImg= hasSymbol.querySelector("img");
        const detailsImg= ownDetails.querySelector("img");
        const lowerPW = password.toLowerCase();
        let strength = 0;
        if (password.length >= 8) {
            minChar.style.color="green"
            minCharImg.src = "image/icons/password-tick.svg";
            passwordError.minChar = true;

            strength++;
        }
        else{
            minChar.style.color="red"
            minCharImg.src = "image/icons/password-cross.svg";
            passwordError.minChar = false;
        }
        if (/[A-Z]/.test(password)) {
            strength++;
        }
        if (/[a-z]/.test(password)) {
            strength++;
        }

        if (/[A-Z]/.test(password) &&/[a-z]/.test(password) ){
            hasLetterCase.style.color="green";
            letterCaseImg.src = "image/icons/password-tick.svg";
            passwordError.letterCase = true;
        }
        else{
            hasLetterCase.style.color="red";
            letterCaseImg.src = "image/icons/password-cross.svg";
            passwordError.letterCase = false;
        }
        if (/\d/.test(password)) {
            hasNumber.style.color="green";
            numberImg.src= "image/icons/password-tick.svg";
            passwordError.hasNumber = true;
            strength++;
        }
        else{
            hasNumber.style.color="red";
            numberImg.src= "image/icons/password-cross.svg";
            passwordError.hasNumber = false;
        }
        if (/[\W_]/.test(password)) {
            hasSymbol.style.color="green"
            symbolImg.src="image/icons/password-tick.svg";
            passwordError.hasSymbol = true;
            strength++;
        }
        else{
            hasSymbol.style.color="red";
            symbolImg.src="image/icons/password-cross.svg";
            passwordError.hasSymbol = false;
        }

        if (!lowerPW.includes(FIRST_NAME) && !lowerPW.includes(LAST_NAME) && !lowerPW.includes(EMAIL)) {
            ownDetails.style.color="green"
            detailsImg.src= "image/icons/password-tick.svg";
            passwordError.ownDetails = true;
            strength++;
        }
        else{
            ownDetails.style.color="red"
            detailsImg.src="image/icons/password-cross.svg";
            passwordError.ownDetails = false;
        }

        if (strength < 3) {
            bar.classList.remove("medium");
            bar.classList.remove("strong");
            bar.classList.add("weak");
            bar.style.width = "20%";
        } else if (strength < 6) {
            bar.classList.remove("strong");
            bar.classList.remove("weak");
            bar.classList.add("medium");
            bar.style.width = "50%";
        } else {
            bar.classList.remove("medium");
            bar.classList.remove("weak");
            bar.classList.add("strong");
            bar.style.width = "100%";
        }
    } else {
        bar.classList.remove("medium");
        bar.classList.remove("strong");
        bar.classList.remove("weak");
        bar.style.width = "0%";
    }
}