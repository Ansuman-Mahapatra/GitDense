import { useState, useEffect, useRef } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { Github, AlertTriangle, CheckCircle, ArrowRight, Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/lib/auth";
import { API_URL } from "@/config";

export function GitHubVerificationOverlay() {
    const { githubVerificationRequired, signInWithGitHub, user, token, setToken } = useAuth();
    const [isWaiting, setIsWaiting] = useState(false);
    const [verified, setVerified] = useState(false);
    const pollIntervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

    // Start polling the backend every 3 seconds after user clicks "Verify with GitHub"
    // When lastGithubVerifiedAt changes to recent (< 2 min ago), verification is done.
    const startPolling = () => {
        setIsWaiting(true);
        signInWithGitHub(); // Opens GitHub OAuth in a new browser tab

        pollIntervalRef.current = setInterval(async () => {
            try {
                const res = await fetch(`${API_URL}/api/user/me`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                if (res.ok) {
                    const userData = await res.json();
                    if (userData.lastGithubVerifiedAt) {
                        const verifiedAt = new Date(userData.lastGithubVerifiedAt).getTime();
                        const twoMinsAgo = Date.now() - 2 * 60 * 1000;
                        if (verifiedAt > twoMinsAgo) {
                            // ✅ Verification completed!
                            clearInterval(pollIntervalRef.current!);
                            setVerified(true);
                            // Refresh auth token with updated user data
                            if (token) setToken(token);
                            setTimeout(() => setVerified(false), 3000); // hide success after 3s
                        }
                    }
                }
            } catch {
                // Network error during polling — ignore and keep trying
            }
        }, 3000);
    };

    // Clean up polling on unmount
    useEffect(() => {
        return () => {
            if (pollIntervalRef.current) clearInterval(pollIntervalRef.current);
        };
    }, []);

    if (!githubVerificationRequired || !user) return null;

    return (
        <AnimatePresence>
            <div className="fixed inset-0 z-[100] flex items-center justify-center p-4">
                {/* Backdrop */}
                <motion.div
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    exit={{ opacity: 0 }}
                    className="absolute inset-0 bg-background/80 backdrop-blur-xl"
                />

                {/* Modal */}
                <motion.div
                    initial={{ opacity: 0, scale: 0.9, y: 20 }}
                    animate={{ opacity: 1, scale: 1, y: 0 }}
                    exit={{ opacity: 0, scale: 0.9, y: 20 }}
                    className="relative w-full max-w-md p-8 rounded-3xl border border-primary/20 bg-card shadow-2xl overflow-hidden"
                >
                    {/* Animated top bar */}
                    <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-transparent via-primary to-transparent animate-pulse" />

                    <div className="flex flex-col items-center text-center space-y-6">

                        {/* Icon */}
                        <div className="relative">
                            <div className="w-20 h-20 rounded-2xl bg-primary/10 flex items-center justify-center border border-primary/20">
                                {verified ? (
                                    <CheckCircle className="w-10 h-10 text-green-400" />
                                ) : isWaiting ? (
                                    <Loader2 className="w-10 h-10 text-primary animate-spin" />
                                ) : (
                                    <Github className="w-10 h-10 text-primary" />
                                )}
                            </div>
                            {!isWaiting && !verified && (
                                <div className="absolute -top-2 -right-2 w-8 h-8 rounded-full bg-yellow-500/10 border border-yellow-500/20 flex items-center justify-center animate-bounce">
                                    <AlertTriangle className="w-4 h-4 text-yellow-500" />
                                </div>
                            )}
                        </div>

                        {/* Text */}
                        <div className="space-y-2">
                            {verified ? (
                                <>
                                    <h2 className="text-2xl font-bold tracking-tight text-green-400">
                                        Verified Successfully!
                                    </h2>
                                    <p className="text-muted-foreground">
                                        You can close the GitHub tab. Returning to app...
                                    </p>
                                </>
                            ) : isWaiting ? (
                                <>
                                    <h2 className="text-2xl font-bold tracking-tight">
                                        Waiting for GitHub...
                                    </h2>
                                    <p className="text-muted-foreground">
                                        A browser tab has opened. Complete the GitHub verification there,
                                        then <strong className="text-primary">close that tab</strong> and
                                        come back — this screen will update automatically.
                                    </p>
                                </>
                            ) : (
                                <>
                                    <h2 className="text-2xl font-bold tracking-tight">
                                        GitHub Verification Required
                                    </h2>
                                    <p className="text-muted-foreground">
                                        {user.githubId
                                            ? "Your GitHub session has expired (3-day security check). Please re-verify to continue."
                                            : "To access repository features, you must link your GitHub account."}
                                    </p>
                                </>
                            )}
                        </div>

                        {/* Action */}
                        {!verified && (
                            <div className="w-full space-y-3">
                                {isWaiting ? (
                                    <div className="flex items-center justify-center gap-2 text-sm text-muted-foreground py-3">
                                        <Loader2 className="w-4 h-4 animate-spin text-primary" />
                                        Checking verification status...
                                    </div>
                                ) : (
                                    <Button
                                        onClick={startPolling}
                                        className="w-full h-12 text-lg font-bold rounded-xl group"
                                    >
                                        Verify with GitHub
                                        <ArrowRight className="ml-2 w-5 h-5 transition-transform group-hover:translate-x-1" />
                                    </Button>
                                )}
                                <p className="text-xs text-muted-foreground">
                                    {isWaiting
                                        ? "Complete verification in the browser tab that just opened."
                                        : "A browser tab will open for secure GitHub authentication."}
                                </p>
                            </div>
                        )}
                    </div>
                </motion.div>
            </div>
        </AnimatePresence>
    );
}
