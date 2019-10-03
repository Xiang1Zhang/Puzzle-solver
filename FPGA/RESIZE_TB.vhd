library IEEE;
use IEEE.std_logic_1164.all;
use ieee.numeric_std.all ;
use work.all;

entity RESIZE_TB is
end entity;

architecture A of RESIZE_TB is
	signal img_clk : std_logic := '0';
	signal itr : std_logic := '0';
	signal itr_rdy : std_logic;
	signal s_clk : std_logic := '0';
	signal s_reset : std_logic := '1';
	signal r_wr : std_logic := '0';
	signal img_dat : std_logic_vector(7 downto 0) := X"00";
	signal r_dat : std_logic_vector(7 downto 0) := X"00";
	signal r_adr : std_logic_vector(16 downto 0) := '0'&X"0000";

	signal F : boolean := true;
begin

	e_rs : entity work.RESIZE(A) port map(
		img_clk, img_dat,
		itr, itr_rdy, s_clk, s_reset,
		r_dat, r_adr, r_wr);

	s_clk <= not s_clk after 20 ns when F;

	F <= true when itr_rdy='0' else false;

	process begin
		s_reset<='0'; 
		wait for 1 us;
		s_reset<='1';
		wait for 1 us;
		itr<='1';
		
		wait for 1 us;
		for I in 0 to 2**18 loop
			img_dat <= std_logic_vector(to_unsigned(I, 8));
			if(F) then img_clk <= '1'; else exit; end if;
			wait for 200 ns;
			img_clk <= '0';
			wait for 200 ns; 
		end loop;
		wait;
	end process;

end architecture;