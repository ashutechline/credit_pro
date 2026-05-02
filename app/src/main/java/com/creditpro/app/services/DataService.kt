package com.creditpro.app.services

import com.creditpro.app.models.*
import kotlin.math.pow

object DataService {

    // ── Banks ─────────────────────────────────────────────────────────────────
    val banks: List<Bank> get() = TranslationManager.getBanks().ifEmpty { staticBanks }
    
    private val staticBanks = listOf(
        Bank("Chase Bank", "USA", "1-800-935-9935", "🏛️", "chase.com"),
        Bank("Bank of America", "USA", "1-800-432-1000", "🏦", "bankofamerica.com"),
        Bank("Wells Fargo", "USA", "1-800-869-3557", "🔴", "wellsfargo.com"),
        Bank("Citibank", "USA", "1-800-374-9700", "🌐", "citibank.com"),
        Bank("Goldman Sachs", "USA", "1-855-730-7283", "💼", "goldmansachs.com"),
        Bank("HSBC", "UK", "1-800-975-4722", "🔺", "hsbc.com"),
        Bank("Barclays", "UK", "1-888-710-8756", "🏴", "barclays.com"),
        Bank("Deutsche Bank", "Germany", "1-844-428-8472", "🇩🇪", "db.com"),
        Bank("Morgan Stanley", "USA", "1-800-869-6397", "📊", "morganstanley.com"),
        Bank("Standard Chartered", "UK", "1-800-572-5678", "🌏", "sc.com"),
        Bank("Santander", "Spain", "1-877-768-2265", "🔥", "santander.com"),
        Bank("BNP Paribas", "France", "1-800-268-2148", "🇫🇷", "bnpparibas.com"),
        Bank("ICBC", "China", "95588", "🇨🇳", "icbc.com.cn"),
        Bank("Mitsubishi UFJ", "Japan", "0120-860-777", "🇯🇵", "mufg.jp"),
        Bank("Royal Bank of Canada", "Canada", "1-800-769-2511", "🍁", "rbc.com"),
        Bank("ANZ Bank", "Australia", "13 13 14", "🦘", "anz.com"),
        Bank("SBI", "India", "1800-425-3800", "🇮🇳", "sbi.co.in"),
        Bank("HDFC Bank", "India", "1800-202-6161", "💙", "hdfcbank.com"),
        Bank("UBS", "Switzerland", "+41-44-234-1111", "🇨🇭", "ubs.com"),
        Bank("Credit Suisse", "Switzerland", "+41-44-333-1111", "❄️", "credit-suisse.com")
    )

    // ── Calculators ───────────────────────────────────────────────────────────
    val calculators: List<Calculator> get() = TranslationManager.getCalculators().ifEmpty { staticCalculators }
    
    private val staticCalculators = listOf(
        Calculator("emi", "EMI Calculator", "Monthly installment on any loan", "💳", "Loan & EMI"),
        Calculator("loan", "Loan Eligibility", "Check max loan eligibility", "🏠", "Loan & EMI"),
        Calculator("vehicle", "Vehicle Loan", "Auto & bike financing", "🚗", "Loan & EMI"),
        Calculator("prepay", "Loan Prepayment", "Save on interest early", "⚡", "Loan & EMI"),
        Calculator("sip", "SIP Calculator", "Systematic investment plan", "📈", "Investment & Savings"),
        Calculator("mutual", "Mutual Funds", "Fund growth estimator", "📊", "Investment & Savings"),
        Calculator("fd", "FD Calculator", "Fixed deposit returns", "🏦", "Investment & Savings"),
        Calculator("rd", "RD Calculator", "Recurring deposit", "💰", "Investment & Savings"),
        Calculator("roi", "ROI Calculator", "Return on investment", "🎯", "Investment & Savings"),
        Calculator("epf", "EPF Calculator", "Provident fund estimate", "👴", "Investment & Savings"),
        Calculator("gst", "GST Calculator", "Goods & services tax", "🧾", "Tax & Salary"),
        Calculator("sales_tax", "Sales Tax", "Regional sales tax", "🏷️", "Tax & Salary"),
        Calculator("salary", "Salary Calculator", "Net take-home pay", "💼", "Tax & Salary"),
        Calculator("boost", "Boost My Score", "Simulate score improvement", "🚀", "Credit Tools")
    )

    // ── Tips ──────────────────────────────────────────────────────────────────
    val tips: List<Tip> get() = TranslationManager.getTips().ifEmpty { staticTips }
    
    private val staticTips = listOf(
        Tip(1, "Keep utilization below 30%", "Credit utilization is the second biggest factor in your score. Keeping balances low relative to your limit signals responsible usage. Aim for under 10% for the best results.", "Credit Score", "💳"),
        Tip(2, "Set up autopay today", "Payment history accounts for 35% of your FICO score. One missed payment can drop your score by up to 100 points. Setting autopay for at least the minimum payment protects you automatically.", "Credit Score", "📅"),
        Tip(3, "Don't close old accounts", "The age of your oldest account matters. Closing a card you've had for years shortens your average credit history — even if you never use the card.", "Credit Score", "🕒"),
        Tip(4, "Diversify your credit mix", "Having both revolving credit (cards) and installment loans demonstrates you can handle multiple types responsibly — boosting your score up to 10%.", "Credit Score", "🔀"),
        Tip(5, "Build a 3-month emergency fund", "Financial advisors recommend having 3–6 months of expenses saved. This prevents you from relying on credit cards during emergencies.", "Savings", "🛡️"),
        Tip(6, "Start SIP as early as possible", "Thanks to compound interest, starting a SIP of \$100/month at age 25 instead of 35 can result in nearly 3x more wealth by retirement.", "Investment", "🌱"),
        Tip(7, "Review your credit report annually", "Errors on credit reports are common. You're entitled to one free report per year from each bureau — check for errors and dispute inaccuracies.", "Credit Score", "📋"),
        Tip(8, "Negotiate lower interest rates", "If you have a good payment history, call your card issuer and ask for a lower APR. Many will agree — a lower rate reduces interest drag.", "Savings", "📞")
    )

    // ── FAQs ──────────────────────────────────────────────────────────────────
    val faqs: List<Pair<String, String>> get() = TranslationManager.getFaqs().ifEmpty { staticFaqs }
    
    private val staticFaqs = listOf(
        Pair("What is a good credit score?", "Scores above 670 are considered good. Above 740 is very good, and above 800 is exceptional. Lenders offer the best rates to those with 740+."),
        Pair("How often does my score update?", "Your credit score typically updates monthly when lenders report your account activity to the credit bureaus."),
        Pair("Does checking my own score hurt it?", "No! Checking your own score is a 'soft inquiry' and has no impact on your credit score whatsoever."),
        Pair("How long do negative items stay?", "Most negative items stay for 7 years. Bankruptcies can stay for up to 10 years."),
        Pair("What is credit utilization?", "Credit utilization is the ratio of your current credit card balances to your credit limits. Lower is better — aim for under 30%."),
        Pair("Can I get a loan with a poor score?", "Yes, but you'll face higher interest rates. Secured loans or credit-builder loans are good options to improve your score while borrowing.")
    )

    // ── Report Questions ──────────────────────────────────────────────────────
    val reportQuestions: List<ReportQuestion> get() = TranslationManager.getReportQuestions().ifEmpty { staticReportQuestions }
    
    private val staticReportQuestions = listOf(
        ReportQuestion("Report Purpose", "Why are you checking your credit report?", "purpose", listOf(
            ReportOption("🏠", "Applying for a mortgage", "mortgage"),
            ReportOption("🚗", "Auto loan application", "auto"),
            ReportOption("💳", "Getting a new credit card", "card"),
            ReportOption("📊", "General monitoring", "monitoring")
        )),
        ReportQuestion("Payment History", "How consistently do you make on-time payments?", "payment_history", listOf(
            ReportOption("✅", "Always on time (100%)", "always"),
            ReportOption("🟡", "Usually on time (90%+)", "usually"),
            ReportOption("⚠️", "Sometimes late (70–90%)", "sometimes"),
            ReportOption("❌", "Frequently late (<70%)", "rarely")
        )),
        ReportQuestion("Credit Utilization", "What percentage of your credit limit do you use?", "utilization", listOf(
            ReportOption("🌟", "Under 10% — Excellent", "under_10"),
            ReportOption("✅", "Under 30% — Good", "under_30"),
            ReportOption("🟡", "Under 50% — Fair", "under_50"),
            ReportOption("🔴", "Over 50% — High", "over_50")
        )),
        ReportQuestion("Account Age", "How old is your oldest credit account?", "account_age", listOf(
            ReportOption("🕰️", "Over 10 years", "over_10"),
            ReportOption("⏰", "5–10 years", "5_10"),
            ReportOption("🕑", "3–5 years", "3_5"),
            ReportOption("🕐", "Under 3 years", "under_3")
        )),
        ReportQuestion("Income Range", "What is your approximate annual income?", "income_range", listOf(
            ReportOption("💰", "Over \$100,000", "high"),
            ReportOption("💵", "\$50,000–\$100,000", "mid"),
            ReportOption("💳", "\$25,000–\$50,000", "low_mid"),
            ReportOption("📉", "Under \$25,000", "low")
        )),
        ReportQuestion("Spending Habits", "How would you describe your monthly spending?", "spending", listOf(
            ReportOption("🧘", "Very disciplined saver", "saver"),
            ReportOption("⚖️", "Balanced spender", "balanced"),
            ReportOption("🛍️", "Occasional overspending", "occasional"),
            ReportOption("🔥", "Frequent overspending", "frequent")
        )),
        ReportQuestion("Credit Mix", "What types of credit do you have?", "credit_mix", listOf(
            ReportOption("🏆", "Cards + Loans + Mortgage", "excellent_mix"),
            ReportOption("✅", "Cards + Installment Loans", "good_mix"),
            ReportOption("💳", "Credit Cards only", "cards_only"),
            ReportOption("❌", "No credit accounts yet", "none")
        )),
        ReportQuestion("Recent Applications", "How many new credit applications in the past year?", "hard_inquiries", listOf(
            ReportOption("✅", "None — no new applications", "zero"),
            ReportOption("1️⃣", "1–2 applications", "one_two"),
            ReportOption("⚠️", "3–5 applications", "three_five"),
            ReportOption("🔴", "More than 5", "many")
        )),
        ReportQuestion("Financial Stress", "Have you faced financial difficulty recently?", "stress_level", listOf(
            ReportOption("😊", "No issues at all", "none"),
            ReportOption("😐", "Minor challenges", "minor"),
            ReportOption("😟", "Significant difficulties", "significant"),
            ReportOption("😰", "Serious hardship / Collections", "serious")
        )),
        ReportQuestion("Financial Goals", "What is your primary financial goal this year?", "goal", listOf(
            ReportOption("🏠", "Buy a home", "home"),
            ReportOption("📈", "Build investments", "invest"),
            ReportOption("💳", "Improve credit score", "improve_credit"),
            ReportOption("🚗", "Major purchase / Loan", "purchase")
        ))
    )

    // ── Score Simulator ───────────────────────────────────────────────────────
    fun simulateScore(
        paymentHistory: Float,
        utilization: Float,
        accountAge: Int,
        inquiries: Int,
        creditMix: Int
    ): Int {
        var score = 300.0
        score += paymentHistory * 1.8
        score += (100 - utilization) * 1.05
        score += (accountAge * 8).coerceAtMost(80)
        score -= inquiries * 12
        score += (creditMix * 12).coerceAtMost(60)
        return score.toInt().coerceIn(300, 850)
    }

    // ── Report Score Computation ──────────────────────────────────────────────
    fun computeReportScore(answers: Map<String, String>): Int {
        var base = 500
        when (answers["payment_history"]) {
            "always" -> base += 80
            "usually" -> base += 40
            "sometimes" -> base += 0
            "rarely" -> base -= 30
        }
        when (answers["utilization"]) {
            "under_10" -> base += 70
            "under_30" -> base += 40
            "under_50" -> base += 10
            "over_50" -> base -= 20
        }
        when (answers["account_age"]) {
            "over_10" -> base += 60
            "5_10" -> base += 40
            "3_5" -> base += 20
            "under_3" -> base += 5
        }
        when (answers["income_range"]) {
            "high" -> base += 30
            "mid" -> base += 15
        }
        when (answers["stress_level"]) {
            "serious" -> base -= 30
            "significant" -> base -= 15
        }
        return base.coerceIn(300, 850)
    }

    // ── Calculator Engine ─────────────────────────────────────────────────────
    fun calculate(id: String, inputs: Map<String, Double>): CalcResult? {
        return try {
            when (id) {
                "emi" -> calcEmi(inputs["principal"] ?: return null, inputs["rate"] ?: return null, inputs["months"] ?: return null)
                "loan" -> calcLoanEligibility(inputs["income"] ?: return null, inputs["expenses"] ?: return null, inputs["rate"] ?: return null)
                "vehicle" -> calcVehicle(inputs["price"] ?: return null, inputs["down"] ?: return null, inputs["rate"] ?: return null, inputs["months"] ?: return null)
                "prepay" -> calcPrepay(inputs["balance"] ?: return null, inputs["rate"] ?: return null, inputs["extra"] ?: return null)
                "sip" -> calcSip(inputs["monthly"] ?: return null, inputs["rate"] ?: return null, inputs["years"] ?: return null)
                "mutual" -> calcMutual(inputs["investment"] ?: return null, inputs["rate"] ?: return null, inputs["years"] ?: return null)
                "fd" -> calcFd(inputs["principal"] ?: return null, inputs["rate"] ?: return null, inputs["years"] ?: return null)
                "rd" -> calcRd(inputs["monthly"] ?: return null, inputs["rate"] ?: return null, inputs["months"] ?: return null)
                "roi" -> calcRoi(inputs["invested"] ?: return null, inputs["finalVal"] ?: return null, inputs["years"] ?: return null)
                "epf" -> calcEpf(inputs["salary"] ?: return null, inputs["rate"] ?: return null, inputs["years"] ?: return null)
                "gst" -> calcGst(inputs["amount"] ?: return null, inputs["rate"] ?: return null)
                "sales_tax" -> calcSalesTax(inputs["amount"] ?: return null, inputs["rate"] ?: return null)
                "salary" -> calcSalary(inputs["gross"] ?: return null, inputs["tax"] ?: return null, inputs["deductions"] ?: return null)
                "boost" -> calcBoost(inputs["current"] ?: return null, inputs["targetUtil"] ?: return null, inputs["newAccounts"] ?: return null)
                else -> null
            }
        } catch (e: Exception) { null }
    }

    private fun fmt(v: Double, prefix: String = "$"): String =
        "$prefix${"%,.2f".format(v)}"

    private fun calcEmi(p: Double, rate: Double, months: Double): CalcResult {
        val r = rate / 12 / 100
        val emi = p * r * (1 + r).pow(months) / ((1 + r).pow(months) - 1)
        return CalcResult(TranslationManager.getLabel("monthly_emi", "Monthly EMI"), fmt(emi), listOf(
            TranslationManager.getLabel("total_payment", "Total Payment") to fmt(emi * months),
            TranslationManager.getLabel("total_interest", "Total Interest") to fmt(emi * months - p),
            TranslationManager.getLabel("loan_amount", "Principal") to fmt(p)
        ))
    }

    private fun calcLoanEligibility(income: Double, expenses: Double, rate: Double): CalcResult {
        val surplus = income - expenses
        val maxEmi = surplus * 0.4
        val r = rate / 12 / 100
        val n = 240.0
        val loan = maxEmi * ((1 + r).pow(n) - 1) / (r * (1 + r).pow(n))
        return CalcResult(TranslationManager.getLabel("max_loan_eligible", "Max Loan Eligible"), fmt(loan), listOf(
            TranslationManager.getLabel("max_monthly_emi", "Max Monthly EMI") to fmt(maxEmi),
            TranslationManager.getLabel("disposable_income", "Disposable Income") to fmt(surplus)
        ))
    }

    private fun calcVehicle(price: Double, down: Double, rate: Double, months: Double): CalcResult {
        val p = price - down
        val r = rate / 12 / 100
        val emi = p * r * (1 + r).pow(months) / ((1 + r).pow(months) - 1)
        return CalcResult(TranslationManager.getLabel("monthly_emi", "Monthly EMI"), fmt(emi), listOf(
            TranslationManager.getLabel("loan_amount", "Loan Amount") to fmt(p),
            TranslationManager.getLabel("total_interest", "Total Interest") to fmt(emi * months - p),
            TranslationManager.getLabel("total_cost", "Total Cost") to fmt(price + (emi * months - p))
        ))
    }

    private fun calcPrepay(b: Double, rate: Double, extra: Double): CalcResult {
        val savings = extra * rate / 100
        return CalcResult(TranslationManager.getLabel("interest_saved", "Interest Saved (Year)"), fmt(savings), listOf(
            TranslationManager.getLabel("outstanding_balance", "Outstanding Balance") to fmt(b),
            TranslationManager.getLabel("extra", "Extra Payment") to fmt(extra),
            TranslationManager.getLabel("new_balance", "New Balance") to fmt(b - extra)
        ))
    }

    private fun calcSip(monthly: Double, rate: Double, years: Double): CalcResult {
        val n = years * 12
        val r = rate / 12 / 100
        val mv = monthly * ((1 + r).pow(n) - 1) / r * (1 + r)
        val totalInvested = monthly * n
        return CalcResult(TranslationManager.getLabel("maturity_amount", "Maturity Amount"), fmt(mv), listOf(
            TranslationManager.getLabel("total_invested", "Total Invested") to fmt(totalInvested),
            TranslationManager.getLabel("total_gains", "Total Gains") to fmt(mv - totalInvested)
        ))
    }

    private fun calcMutual(inv: Double, rate: Double, years: Double): CalcResult {
        val mv = inv * (1 + rate / 100).pow(years)
        return CalcResult(TranslationManager.getLabel("maturity_amount", "Maturity Amount"), fmt(mv), listOf(
            TranslationManager.getLabel("initial_investment", "Initial Investment") to fmt(inv),
            TranslationManager.getLabel("total_gains", "Total Gains") to fmt(mv - inv)
        ))
    }

    private fun calcFd(principal: Double, rate: Double, years: Double): CalcResult {
        val maturity = principal * (1 + rate / 100).pow(years)
        return CalcResult(TranslationManager.getLabel("maturity_amount", "Maturity Amount"), fmt(maturity), listOf(
            TranslationManager.getLabel("principal", "Principal") to fmt(principal),
            TranslationManager.getLabel("interest_earned", "Interest Earned") to fmt(maturity - principal)
        ))
    }

    private fun calcRd(monthly: Double, rate: Double, months: Double): CalcResult {
        val r = rate / 12 / 100
        var maturity = 0.0
        for (i in 1..months.toInt()) maturity += monthly * (1 + r).pow(months - i + 1)
        val invested = monthly * months
        return CalcResult(TranslationManager.getLabel("maturity_amount", "Maturity Amount"), fmt(maturity), listOf(
            TranslationManager.getLabel("total_invested", "Total Invested") to fmt(invested),
            TranslationManager.getLabel("interest_earned", "Interest Earned") to fmt(maturity - invested)
        ))
    }

    private fun calcRoi(invested: Double, finalVal: Double, years: Double): CalcResult {
        if (invested <= 0.0 || years <= 0.0) {
            return CalcResult(TranslationManager.getLabel("total_roi", "Total ROI"), "0.00%",
                listOf(TranslationManager.getLabel("cagr", "CAGR (Annualized)") to "0.00%", TranslationManager.getLabel("profit", "Profit") to fmt(0.0)))
        }
        val roi = (finalVal - invested) / invested * 100
        val cagr = ((finalVal / invested).pow(1.0 / years) - 1) * 100
        val profit = finalVal - invested
        return CalcResult(TranslationManager.getLabel("total_roi", "Total ROI"), "${"%.2f".format(roi)}%", listOf(
            TranslationManager.getLabel("cagr", "CAGR (Annualized)") to "${"%.2f".format(cagr)}%",
            TranslationManager.getLabel("profit", "Profit") to fmt(profit)
        ))
    }

    private fun calcEpf(salary: Double, rate: Double, years: Double): CalcResult {
        val monthly = salary * 0.24
        val r = (if (rate > 0) rate else 8.15) / 12 / 100
        val n = years * 12
        val corpus = monthly * ((1 + r).pow(n) - 1) / r
        return CalcResult(TranslationManager.getLabel("epf_corpus", "EPF Corpus"), fmt(corpus), listOf(
            TranslationManager.getLabel("monthly_contribution", "Monthly Contribution") to fmt(monthly),
            TranslationManager.getLabel("years", "Years") to "${years.toInt()}"
        ))
    }

    private fun calcGst(a: Double, rate: Double): CalcResult {
        val gst = a * rate / 100
        return CalcResult(TranslationManager.getLabel("gst_amount", "GST Amount"), fmt(gst), listOf(
            TranslationManager.getLabel("total_incl_gst", "Total (incl. GST)") to fmt(a + gst),
            TranslationManager.getLabel("cgst", "CGST") to fmt(gst / 2),
            TranslationManager.getLabel("sgst", "SGST") to fmt(gst / 2)
        ))
    }

    private fun calcSalesTax(a: Double, rate: Double): CalcResult {
        val tax = a * rate / 100
        return CalcResult(TranslationManager.getLabel("tax_amount", "Tax Amount"), fmt(tax), listOf(
            TranslationManager.getLabel("original_price", "Original Price") to fmt(a),
            TranslationManager.getLabel("final_price", "Final Price") to fmt(a + tax)
        ))
    }

    private fun calcSalary(gross: Double, taxRate: Double, ded: Double): CalcResult {
        val tax = gross * taxRate / 100
        val net = gross - tax - ded
        return CalcResult(TranslationManager.getLabel("net_annual_salary", "Net Annual Salary"), fmt(net), listOf(
            TranslationManager.getLabel("monthly_take_home", "Monthly Take-Home") to fmt(net / 12),
            TranslationManager.getLabel("tax_deducted", "Tax Deducted") to fmt(tax),
            TranslationManager.getLabel("other_deductions", "Other Deductions") to fmt(ded)
        ))
    }

    private fun calcBoost(current: Double, targetUtil: Double, newAccounts: Double): CalcResult {
        val boost = (100 - targetUtil) * 0.8 - newAccounts * 5
        val newScore = (current + boost).coerceIn(300.0, 850.0)
        return CalcResult(TranslationManager.getLabel("projected_score", "Projected Score"), "${newScore.toInt()}", listOf(
            TranslationManager.getLabel("estimated_boost", "Estimated Boost") to "+${boost.toInt()}",
            TranslationManager.getLabel("targetUtil", "Target Utilization") to "${targetUtil.toInt()}%"
        ))
    }
}
