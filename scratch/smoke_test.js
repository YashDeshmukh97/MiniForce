const http = require('http');

async function req(path, method = 'GET', body = null, token = null) {
    return new Promise((resolve, reject) => {
        const options = {
            hostname: 'localhost',
            port: 8080,
            path: path,
            method: method,
            headers: {
                'Content-Type': 'application/json'
            }
        };
        if (token) options.headers['Authorization'] = 'Bearer ' + token;
        
        const request = http.request(options, (res) => {
            let data = '';
            res.on('data', chunk => data += chunk);
            res.on('end', () => resolve({ status: res.statusCode, data: data ? JSON.parse(data) : null }));
        });
        request.on('error', reject);
        if (body) request.write(JSON.stringify(body));
        request.end();
    });
}

async function run() {
    try {
        console.log("1. Creating user and logging in...");
        const rnd = Math.floor(Math.random() * 100000);
        let authRes = await req('/api/auth/signup', 'POST', {
            username: 'smoke' + rnd,
            email: 'smoke' + rnd + '@test.com',
            password: 'password'
        });
        if (authRes.status !== 200) {
            console.log("Signup failed, falling back to login. Status:", authRes.status);
            authRes = await req('/api/auth/login', 'POST', { username: 'smoke' + rnd, password: 'password' });
        }
        const token = authRes.data.token;
        if (!token) throw new Error("No token returned");
        console.log("Logged in successfully. Token length:", token.length);

        console.log("2. Fetching problems...");
        const probRes = await req('/api/problems', 'GET');
        const problems = probRes.data;
        console.log(`Fetched ${problems.length} problems.`);
        
        // Let's get problems 8, 12, 13, 14, 20
        const realIds = [1, 2, 3, 8, 12, 13, 14, 20];
        
        for (const id of realIds) {
            console.log(`Checking problem ${id}...`);
            const p = await req('/api/problems/' + id, 'GET');
            if (p.status !== 200) throw new Error("Could not fetch problem " + id);
            console.log(`Problem ${id} loaded: ${p.data.title}`);
        }
        
        // Wait, submitting solution to 8
        const codes = {
            8: "class Solution { public int maxProfit(int[] prices) { return 5; } }",
            12: "class Solution { public int climbStairs(int n) { return n; } }",
            13: "class Solution { public int[] productExceptSelf(int[] nums) { return new int[]{24,12,8,6}; } }",
            14: "class Solution { public int maxSubArray(int[] nums) { return 6; } }",
            20: "class Solution { public int longestConsecutive(int[] nums) { return 4; } }"
        };
        
        for (const [id, code] of Object.entries(codes)) {
            console.log(`Submitting problem ${id}...`);
            const subRes = await req('/api/submissions', 'POST', {
                problemId: parseInt(id),
                language: 'java',
                sourceCode: code
            }, token);
            console.log(`Submission for ${id} created: ${subRes.data.id}`);
            
            // Poll
            let status = 'QUEUED';
            let limit = 20;
            while ((status === 'QUEUED' || status === 'COMPILING' || status === 'EXECUTING') && limit > 0) {
                await new Promise(r => setTimeout(r, 1000));
                const statRes = await req('/api/submissions/' + subRes.data.id, 'GET', null, token);
                status = statRes.data.status;
                console.log(`  -> Status: ${status}`);
                limit--;
            }
            if (status !== 'ACCEPTED') throw new Error(`Expected ACCEPTED for ${id}, got ${status}`);
        }
        
        console.log("Submitting wrong answer to problem 8...");
        const waSub = await req('/api/submissions', 'POST', {
            problemId: 8,
            language: 'java',
            sourceCode: "class Solution { public int maxProfit(int[] prices) { return 0; } }"
        }, token);
        let status = 'QUEUED';
        while (status === 'QUEUED' || status === 'COMPILING' || status === 'EXECUTING') {
            await new Promise(r => setTimeout(r, 1000));
            const statRes = await req('/api/submissions/' + waSub.data.id, 'GET', null, token);
            status = statRes.data.status;
        }
        console.log(`  -> WA Status: ${status}`);
        if (status !== 'WRONG_ANSWER') throw new Error(`Expected WRONG_ANSWER, got ${status}`);
        
        console.log("Submitting compilation error to problem 12...");
        const ceSub = await req('/api/submissions', 'POST', {
            problemId: 12,
            language: 'java',
            sourceCode: "class Solution { public int climbStairs(int n) { return x; } }"
        }, token);
        status = 'QUEUED';
        while (status === 'QUEUED' || status === 'COMPILING' || status === 'EXECUTING') {
            await new Promise(r => setTimeout(r, 1000));
            const statRes = await req('/api/submissions/' + ceSub.data.id, 'GET', null, token);
            status = statRes.data.status;
        }
        console.log(`  -> CE Status: ${status}`);
        if (status !== 'COMPILATION_ERROR') throw new Error(`Expected COMPILATION_ERROR, got ${status}`);
        
        console.log("All backend real judge flows verified.");
        
    } catch (e) {
        console.error(e);
        process.exit(1);
    }
}
run();
