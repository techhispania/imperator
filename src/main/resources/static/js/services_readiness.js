document.addEventListener("DOMContentLoaded", () => {
	setInterval(() => {
		console.log("Checking services readiness");
		
		// TODO get all services from html page
		const services = document.querySelectorAll(".service-data");
		
		console.log(`Found ${services.length} services`)
		
		services.forEach(service => {
			
			var id = service.id
			var service_name = id.split("service_")[1]
			var content = service.textContent
			
			console.log(`service ${service_name}: ${content}`)
			
			service_status = checkService(service_name)
			
			// TODO update the html page
		})
	}, 5000);
	
	async function checkService(service_name) {
		try {
			const response = await fetch(`http://localhost:8080/api/service/check`, {
					method: "POST",
					headers: {
						"Content-Type": "application/json",
						"Accept": "appliation/json"
					},
					body: JSON.stringify({
						name: service_name,
					})
				})
				
				
			if (!response.ok) {
				throw new Error(`HTTP error! Status: ${response.status}`)
			}
			
			const data = await response.json()
			console.log(`Port: ${data.port}, PID: ${data.pid}`)
			
			return data
		} catch (err) {
			console.error("Error checking service " + service_name, err)
		}
	}
});