document.addEventListener("DOMContentLoaded", () => {
	setInterval(() => {
		console.log("Checking services readiness");
		
		// TODO get all services from html page
		const services = document.querySelectorAll(".service-data");
		
		console.log(`Found ${services.length} services`)
		
		services.forEach(service => {
			
			var content = service.textContent
			
			console.log(`service: ${content}`)
		})
		
		// TODO POST request to get port from the PID
		
		// TODO update the html page
	}, 5000);
});