import { Copy, Check, Maximize2, Minimize2, Save, Sparkles, Wand2, Info } from "lucide-react";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { ScrollArea } from "@/components/ui/scroll-area";
import { cn } from "@/lib/utils";
import { useState, useMemo } from "react";
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { vscDarkPlus } from 'react-syntax-highlighter/dist/esm/styles/prism';

interface CodeEditorProps {
  initialCode: string;
  language?: string;
  onChange?: (code: string) => void;
  readOnly?: boolean;
  onAiExplain?: (code: string) => void;
}

export function CodeEditor({ initialCode, language = "typescript", onChange, readOnly = true, onAiExplain }: CodeEditorProps) {
  const [isFocused, setIsFocused] = useState(false);
  const [copied, setCopied] = useState(false);

  // Map common extensions or names to prism-supported languages
  const getLanguage = (lang: string) => {
    const map: Record<string, string> = {
      'js': 'javascript', 'javascript': 'javascript',
      'ts': 'typescript', 'typescript': 'typescript',
      'tsx': 'tsx', 'jsx': 'jsx',
      'py': 'python', 'python': 'python',
      'java': 'java', 'cpp': 'cpp', 'c': 'c',
      'go': 'go', 'golang': 'go',
      'rs': 'rust', 'rust': 'rust',
      'yml': 'yaml', 'yaml': 'yaml',
      'md': 'markdown', 'json': 'json', 'xml': 'xml', 'html': 'html',
      'css': 'css', 'sh': 'bash', 'bash': 'bash', 'sql': 'sql', 'php': 'php', 'rb': 'ruby'
    };
    return map[lang.toLowerCase()] || lang.toLowerCase() || 'typescript';
  };

  const handleCopy = () => {
    navigator.clipboard.writeText(initialCode);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  // Divide code into logical "blocks" based on double newlines or structural markers
  const blocks = useMemo(() => {
    if (!initialCode) return [];
    
    const lines = initialCode.split('\n');
    const result: { content: string, startLine: number }[] = [];
    let currentBlock: string[] = [];
    let startLine = 1;

    for (let i = 0; i < lines.length; i++) {
        const line = lines[i];
        currentBlock.push(line);
        
        // Strategy: Separate at empty lines if block is large enough, or at obvious structural breaks
        const isStructuralBreak = (line.trim() === '}' || line.trim() === '};') && 
                                (i < lines.length - 1 && lines[i+1].trim() === '');
        const isEmptyLineBreak = line.trim() === '' && currentBlock.length > 5;
        const isEnd = i === lines.length - 1;

        if ((isStructuralBreak || isEmptyLineBreak || isEnd) && currentBlock.length > 0) {
            result.push({
                content: currentBlock.join('\n'),
                startLine: startLine
            });
            startLine = i + 2;
            currentBlock = [];
        }
    }

    return result;
  }, [initialCode]);

  return (
    <Card className={cn(
      "h-full overflow-hidden flex flex-col border-0 shadow-2xl transition-all duration-300",
      "bg-[#0d0d0d] text-[#d4d4d4]", // Deeper Dark Theme
      isFocused ? "ring-2 ring-emerald-500/30" : ""
    )}>
      {/* Mac-style Window Header */}
      <CardHeader className="flex-row items-center justify-between py-2 px-4 bg-[#1a1a1a] border-b border-white/5 shrink-0 h-10">
        <div className="flex items-center gap-2">
          <div className="flex gap-1.5 mr-4 group">
            <div className="w-3 h-3 rounded-full bg-[#ff5f56] opacity-80" />
            <div className="w-3 h-3 rounded-full bg-[#ffbd2e] opacity-80" />
            <div className="w-3 h-3 rounded-full bg-[#27c93f] opacity-80" />
          </div>
          <Badge variant="outline" className="h-5 px-2 text-[10px] font-mono bg-white/5 border-transparent text-emerald-400">
            {language.toUpperCase()}
          </Badge>
          {!readOnly && <Badge className="h-5 bg-emerald-500/20 text-emerald-400 border-emerald-500/30 text-[10px] px-2 uppercase tracking-wider">Editor</Badge>}
        </div>

        <div className="flex items-center gap-2">
          {onAiExplain && (
            <Button
              variant="ghost"
              size="sm"
              className="h-7 gap-1.5 text-xs text-emerald-400 hover:text-emerald-300 hover:bg-emerald-500/10 border border-emerald-500/20"
              onClick={() => onAiExplain(initialCode)}
            >
              <Sparkles className="w-3.5 h-3.5" />
              <span>Analyze Full File</span>
            </Button>
          )}
          <Button
            variant="ghost"
            size="icon"
            className="h-6 w-6 text-muted-foreground hover:text-white"
            onClick={handleCopy}
            title="Copy All"
          >
            {copied ? <Check className="w-3.5 h-3.5 text-emerald-500" /> : <Copy className="w-3.5 h-3.5" />}
          </Button>
        </div>
      </CardHeader>

      <CardContent className="p-0 flex-1 overflow-hidden relative font-mono text-[13px]">
        {readOnly ? (
          <ScrollArea className="h-full w-full">
            <div className="min-w-max p-4 space-y-4">
              {blocks.map((block, bIdx) => (
                <div key={bIdx} className="group relative border border-white/5 rounded-xl bg-white/[0.02] hover:bg-white/[0.04] transition-all duration-300 p-1">
                  {/* Block Action Button */}
                  {onAiExplain && (
                    <button
                      onClick={(e) => { e.stopPropagation(); onAiExplain(block.content); }}
                      className="absolute right-4 top-4 z-10 opacity-0 group-hover:opacity-100 flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-emerald-500/20 text-emerald-400 border border-emerald-500/30 text-[11px] backdrop-blur-md shadow-xl transition-all hover:bg-emerald-500/30 hover:scale-105"
                      title="Explain this block"
                    >
                      <Sparkles className="w-3.5 h-3.5" />
                      <span className="font-semibold uppercase tracking-tighter">AI Explain</span>
                    </button>
                  )}
                  
                  <SyntaxHighlighter
                    language={getLanguage(language)}
                    style={vscDarkPlus}
                    showLineNumbers={true}
                    startingLineNumber={block.startLine}
                    lineNumberStyle={{
                      minWidth: '3.5em',
                      paddingRight: '1em',
                      color: '#4b5563',
                      textAlign: 'right',
                      userSelect: 'none',
                      borderRight: '1px solid rgba(255,255,255,0.05)',
                      marginRight: '1em',
                    }}
                    customStyle={{
                      margin: 0,
                      padding: '12px 0',
                      background: 'transparent',
                      fontSize: '13px',
                      lineHeight: '1.6',
                    }}
                  >
                    {block.content}
                  </SyntaxHighlighter>
                </div>
              ))}
            </div>
          </ScrollArea>
        ) : (
          <div className="flex h-full w-full bg-[#1e1e1e]">
            <div className="w-12 bg-[#1e1e1e] text-[#858585] text-right pr-3 select-none py-4 border-r border-[#3e3e42]/30 flex flex-col pt-[16px]">
              {initialCode.split("\n").map((_, i) => (
                <div key={i} className="leading-6 h-6">{i + 1}</div>
              ))}
            </div>
            <textarea
              className="flex-1 p-4 bg-transparent text-[#d4d4d4] resize-none focus:outline-none placeholder-muted-foreground/50 leading-6 whitespace-pre font-mono outline-none"
              value={initialCode}
              onChange={(e) => onChange?.(e.target.value)}
              spellCheck={false}
              onFocus={() => setIsFocused(true)}
              onBlur={() => setIsFocused(false)}
            />
          </div>
        )}
      </CardContent>
    </Card>
  );
}
