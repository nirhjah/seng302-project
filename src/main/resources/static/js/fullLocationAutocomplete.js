/**
 *
 * NOTE: This script requires the following HTML attributes to be defined:
 *
 * #address-line-1
 * #autocomplete-container
 * #country
 * #city
 * #suburb
 * #postcode
 *
 */

/**
 * The delay time (in milliseconds) for debouncing the search input.
 * @type {number}
 */
const DEBOUNCE_DELAY = 450; // set the debounce delay to 450ms

const searchBox = document.querySelector('#address-line-1');
const resultsContainer = document.querySelector('#autocomplete-container');
const countryInput = document.querySelector('#country');
const cityInput = document.querySelector('#city');
const suburbInput = document.querySelector('#suburb');
const postcodeInput = document.querySelector('#postcode');


let timeoutId;

/**
 * Debounces the given function with the given delay time.
 * @param {Function} fn - The function to be debounced.
 * @param {number} delay - The delay time (in milliseconds) for debouncing.
 * @returns {Function} The debounced function.
 */
function debounce(fn, delay) {
    return function () {
        clearTimeout(timeoutId);
        timeoutId = setTimeout(() => fn.apply(this, arguments), delay);
    }
}



/**
 * Searches the OpenRouteService Geocoding API for autocomplete results based on the current search input value.
 * Displays the results in the results container element.
 */
function searchAutocomplete() {
    const searchTerm = this.value.trim();

    if (searchTerm.length >= 3) {

        axios.get(ENDPOINT, {
            params: {
                text: searchTerm,
                language: 'en'
            }
        })
            .then(response => {
                const results = response.data.features.slice(0, 5);
                let html = '';
                if (results.length > 0) {
                    html = '<ul>';
                    results.forEach(result => {
                        html += `<li class="location-autocomplete-value" data-country="${result.properties.country}" data-city="${result.properties.locality}" data-suburb="${result.properties.localadmin}" data-postcode="${result.properties.postalcode}" data-house-number="${result.properties.housenumber}" data-street="${result.properties.street}">${result.properties.label}</li>`;
                    });
                    html += '</ul>';
                } else {
                    html = '<p>No results found</p>';
                }
                resultsContainer.innerHTML = html;
            })
            .catch(error => {
                console.error(error);
                resultsContainer.innerHTML = '<p>Error fetching results</p>';
            });
    } else {
        resultsContainer.innerHTML = '';
    }
}

/**
 * Adds an event listener to the search input element for debouncing the searchAutocomplete function.
 */
searchBox.addEventListener('input', debounce(searchAutocomplete, DEBOUNCE_DELAY));




/**
 * Adds an event listener to the results container element for handling the click event on an autocomplete result.
 * Updates the search input and the selected address elements with the clicked result data.
 */
resultsContainer.addEventListener('click', function (event) {
    if (event.target.tagName === 'LI') {
        let address = event.target.getAttribute('data-house-number') === "undefined" ? "" : event.target.getAttribute('data-house-number') + " ";
        address += event.target.getAttribute('data-street') === "undefined" ? "" : event.target.getAttribute('data-street');
        searchBox.value = address;
        const country = event.target.getAttribute('data-country') === "undefined" ? "" : event.target.getAttribute('data-country');
        const city = event.target.getAttribute('data-city') === "undefined" ? "" : event.target.getAttribute('data-city');
        const suburb = event.target.getAttribute('data-suburb') === "undefined" ? "" : event.target.getAttribute('data-suburb');
        const postcode = event.target.getAttribute('data-postcode') === "undefined" ? "" : event.target.getAttribute('data-postcode');
        countryInput.value = country;
        cityInput.value = suburb;
        suburbInput.value = city;
        postcodeInput.value = postcode;
        resultsContainer.innerHTML = "";
    }
});

document.addEventListener('click', function (event) {
    if (resultsContainer.innerHTML !== '' && !resultsContainer.contains(event.target)) {
        resultsContainer.innerHTML = "";
    }
});
